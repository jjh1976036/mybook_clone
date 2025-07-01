package mp.infra;

import lombok.RequiredArgsConstructor;
import mp.domain.*;
import mp.infra.dto.BookHistoryResponse;
import mp.infra.dto.BookReadRequest;
import mp.infra.dto.PurchaseRequest;
import mp.infra.kafka.BookKafkaProducer;
import mp.infra.security.JwtVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mybook")
@RequiredArgsConstructor
public class MyBookController {

    private final MyBookRepository myBookRepository;
    private final BookViewRepository bookViewRepository;
    private final JwtVerifier jwtVerifier;
    private final BookKafkaProducer bookKafkaProducer;
    private final RestTemplate restTemplate;

    @Value("${books.service-url}")
    private String booksServiceUrl;

    // ✅ 도서 구매
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseBook(
            @RequestBody PurchaseRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> userInfo = jwtVerifier.verifyAndExtract(token);
        String userId = (String) userInfo.get("user_id");
        boolean isSubscribed = Boolean.TRUE.equals(userInfo.get("is_subscribed"));

        if (isSubscribed) {
            MyBook myBook = new MyBook();
            myBook.setUserId(userId);
            myBook.setBookId(request.getBookId());
            myBook.setType("PURCHASE");
            myBook.setUsedPoints(0);
            myBook.setCreatedAt(new Date());
            myBookRepository.save(myBook);

            return ResponseEntity.ok(Map.of("message", "✅ 구독자: 구매 완료"));
        }

        // 비구독자: 포인트 요청 발행
        bookKafkaProducer.requestPointCheck(userId, request.getBookId(), request.getPoint());

        return ResponseEntity.accepted().body(Map.of("message", "🕐 비구독자: 포인트 확인 중"));
    }

    // ✅ 구매 이력 조회
    @GetMapping("/history")
    public List<BookHistoryResponse> getBookHistory(@RequestParam("user_id") String userId) {
        return bookViewRepository.findByUserId(userId).stream()
                .map(view -> new BookHistoryResponse(
                        view.getBookId(),
                        view.getAuthorName(),
                        view.getTitle(),
                        view.getCategory(),
                        view.getImageUrl()
                ))
                .collect(Collectors.toList());
    }

    // ✅ 도서 열람 (기록 없이 단순 조회만)
    @PostMapping("/read")
    public ResponseEntity<?> readBook(
            @RequestBody BookReadRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> userInfo = jwtVerifier.verifyAndExtract(token);

        // Books 서비스에서 도서 정보 조회
        String url = booksServiceUrl + "/books/" + request.getBookId();
        Map<String, Object> bookData = restTemplate.getForObject(url, Map.class);

        // 열람 응답만 반환 (저장/이벤트 없음)
        return ResponseEntity.ok(Map.of(
                "content", bookData.get("content"),
                "audio_url", bookData.get("audio_url"),
                "image_url", bookData.get("image_url"),
                "updated_at", ZonedDateTime.now(ZoneId.systemDefault()).toString()
        ));
    }
}




