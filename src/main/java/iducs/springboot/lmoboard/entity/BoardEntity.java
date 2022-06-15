package iducs.springboot.lmoboard.entity;

import iducs.springboot.lmoboard.domain.BoardStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tb201812048_board")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "writer")
public class BoardEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bor_id;

    private String title;
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private BoardStatus status;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩, join에 대해 나중에 필요시에만 사용한다.
    private MemberEntity writer; // 1대 다 관계 연결

    public void changeTitle(String title) {
        this.title = title;
    }

    public void changeContent(String content) {
        this.content = content;
    }
}
