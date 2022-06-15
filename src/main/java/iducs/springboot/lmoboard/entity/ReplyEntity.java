package iducs.springboot.lmoboard.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "tb201812048_reply")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(exclude = "boardEntity")
public class ReplyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long re_id;

    private String text;
    private String replier;

    @ManyToOne(fetch = FetchType.LAZY)
    private BoardEntity boardEntity;
}
