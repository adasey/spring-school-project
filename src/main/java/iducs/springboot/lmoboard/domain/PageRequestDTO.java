package iducs.springboot.lmoboard.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO {
    private int page;
    private int size;
    private int order;
    private String type; // e - email, p - phone, a - address 페이지 조회 기준
    private String keyword; // 검색어

    public PageRequestDTO() {
        this.page = 1;
        this.size = 5;
    }

    public PageRequest getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }
}
