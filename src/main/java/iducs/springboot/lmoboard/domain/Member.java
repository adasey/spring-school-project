package iducs.springboot.lmoboard.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Member {
    private Long seq;
    private String id;
    private String name;
    private String pw;
    private String email;
    private String phone;
    private String address;
    private Status status;
}
