//package mp.domain;
//
//import java.time.LocalDate;
//import java.util.*;
//import lombok.*;
//import mp.domain.*;
//import mp.infra.AbstractEvent;
//
//<<< DDD / Domain Event
//@Data
//@ToString
//public class BookRead extends AbstractEvent {
//
//    private Long id;
//    private String userId;
//    private String bookId;
//
//    private String type;        // ebook, audiobook 등
//    private Date viewedAt;      // 열람 시간
//
//    public BookRead(MyBook aggregate) {
//        super(aggregate);
//        this.id = aggregate.getId();
//        this.userId = aggregate.getUserId();
//        this.bookId = aggregate.getBookId();
//        this.type = aggregate.getType();
//        this.viewedAt = new Date();  // 현재 시각을 열람 시각으로 설정
//    }
//
//    public BookRead() {
//        super();
//    }
//}
//>>> DDD / Domain Event
