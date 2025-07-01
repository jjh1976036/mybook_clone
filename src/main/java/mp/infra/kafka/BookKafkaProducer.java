package mp.infra.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class BookKafkaProducer {

    private static final String TOPIC = "point-request";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void requestPointCheck(String userId, String bookId, int price) {
        Map<String, Object> message = new HashMap<>();
        message.put("user_id", userId);
        message.put("book_id", bookId);
        message.put("price", price);

        kafkaTemplate.send(TOPIC, message);
    }
}
