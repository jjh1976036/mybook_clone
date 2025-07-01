package mp.domain;

import lombok.Data;
import mp.MybookApplication;
import mp.domain.BookPurchased;
// import mp.domain.BookRead;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "MyBook_table")
@Data
public class MyBook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String userId;
    private String bookId;
    private Date createdAt;

    private Integer usedPoints; // 구매 시 사용한 포인트
    private String type;        // "PURCHASE" 또는 "READ"

    @PrePersist
    public void prePersist() {
        this.createdAt = new Date();
    }

    @PostPersist
    public void onPostPersist() {
        // 도서 구매 시에만 BookPurchased 이벤트 발행
        if ("PURCHASE".equalsIgnoreCase(this.type)) {
            BookPurchased bookPurchased = new BookPurchased(this);
            bookPurchased.publishAfterCommit();
        }

        // 도서 열람은 controller/service 단에서 수동으로 BookRead 발행
    }

    public static MyBookRepository repository() {
        return MybookApplication.applicationContext.getBean(MyBookRepository.class);
    }
}

