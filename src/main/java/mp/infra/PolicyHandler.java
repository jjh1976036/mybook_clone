package mp.infra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import mp.domain.MyBook;
import mp.domain.MyBookRepository;

@Component
public class PolicyHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "mp", groupId = "mybook")
    public void listen(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String eventType = json.get("eventType").asText();

            System.out.println("🔔 [PolicyHandler] Received event: " + eventType);
        } catch (Exception e) {
            System.err.println("❌ Failed to parse mp message: " + e.getMessage());
        }
    }

    // ⬇️ 이게 새로 추가할 메서드!
    @KafkaListener(topics = "point-response", groupId = "mybook")
    public void handlePointResponse(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String userId = json.get("user_id").asText();
            String bookId = json.get("book_id").asText();
            int point = json.get("point").asInt();

            System.out.println("💰 [PointResponse] user_id: " + userId + ", book_id: " + bookId + ", point: " + point);

            if (point == 0) {
                MyBook myBook = new MyBook();
                myBook.setUserId(userId);
                myBook.setBookId(bookId);
                myBook.setUsedPoints(0);
                myBook.setType("PURCHASE");

                MyBook.repository().save(myBook); // 기존 정적 접근 방식 사용
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to parse point-response message: " + e.getMessage());
        }
    }
}


