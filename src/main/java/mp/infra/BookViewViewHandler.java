//package mp.infra;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import mp.domain.BookPurchased;
//import mp.domain.BookRead;
//import mp.domain.BookView;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class BookViewViewHandler {
//
//    @Autowired
//    private BookViewRepository bookViewRepository;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @KafkaListener(topics = "mp", groupId = "mybook")
//    public void handleKafkaMessage(String message) {
//        try {
//            JsonNode jsonNode = objectMapper.readTree(message);
//            String eventType = jsonNode.get("eventType").asText();
//
//            if ("BookPurchased".equals(eventType)) {
//                BookPurchased event = objectMapper.treeToValue(jsonNode, BookPurchased.class);
//                handleBookPurchased(event);
//            } else if ("BookRead".equals(eventType)) {
//                BookRead event = objectMapper.treeToValue(jsonNode, BookRead.class);
//                handleBookRead(event);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace(); // TODO: proper logging
//        }
//    }
//
//    private void handleBookPurchased(BookPurchased event) {
//        if (!event.validate()) return;
//
//        List<BookView> bookViewList = bookViewRepository.findByUserId(event.getUserId());
//        for (BookView bookView : bookViewList) {
//            bookView.setBookStatus("PURCHASED");
//            bookViewRepository.save(bookView);
//        }
//    }
//
//    private void handleBookRead(BookRead event) {
//        if (!event.validate()) return;
//
//        List<BookView> bookViewList = bookViewRepository.findByUserId(event.getUserId());
//        for (BookView bookView : bookViewList) {
//            bookView.setBookStatus("READ");
//            bookViewRepository.save(bookView);
//        }
//    }
//}

package mp.infra;

import java.util.List;
import mp.domain.BookPurchased;
import mp.domain.BookView;
import mp.infra.BookViewRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class BookViewViewHandler {

    @Autowired
    private BookViewRepository bookViewRepository;

    @KafkaListener(topics = "mp", groupId = "mybook", containerFactory = "kafkaListenerContainerFactory")
    public void handleBookPurchased(BookPurchased event) {
        if (!event.validate()) return;

        if (event.getPoint() != 0) return; // 🔍 포인트가 0일 때만 구매 상태로 설정

        List<BookView> bookViewList = bookViewRepository.findByUserId(event.getUserId());
        for (BookView bookView : bookViewList) {
            if (bookView.getBookId().equals(event.getBookId())) {
                bookView.setBookStatus("PURCHASED");
                bookViewRepository.save(bookView);
            }
        }
    }
}




