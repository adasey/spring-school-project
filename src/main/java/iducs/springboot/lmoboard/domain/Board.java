package iducs.springboot.lmoboard.domain;

import lombok.*;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private Long bor_id;
    private String title;
    private String content;
    private BoardStatus status;

    private Long writerSeq;
    private String writerId;
    private String writerName;
    private String writerEmail;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private int replyCount;
}
