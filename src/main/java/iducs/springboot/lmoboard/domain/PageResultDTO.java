package iducs.springboot.lmoboard.domain;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> {
    private List<DTO> dtoList;
    private int totalPage;

    private int currentPage;
    private int sizeOfPage;
    private int numberOfPage;

    private int startPage, endPage;
    private boolean firstPage, lastPage;
    private boolean prevPage, nextPage;
    private List<Integer> pageList;

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn) {
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    public void makePageList(Pageable pageable) {
        this.setPageSizeAndCurrent(pageable);
        double pageDouble = (double) sizeOfPage;
        this.setNumberOfPage(5);
        int tempEnd = (int)(Math.ceil(currentPage/pageDouble)) * sizeOfPage;

        startPage = tempEnd - (sizeOfPage - 1);
        endPage = (totalPage > tempEnd) ? tempEnd: totalPage;
        prevPage = startPage > 1;
        nextPage = totalPage > tempEnd;

        firstPage = prevPage;
        lastPage = nextPage;

        pageList = IntStream.rangeClosed(startPage, endPage).boxed().collect(Collectors.toList());
    }

    public void setPageSizeAndCurrent(Pageable pageable) {
        this.currentPage = pageable.getPageNumber() + 1;
        this.setNumberOfPage(5);
        this.sizeOfPage = this.numberOfPage;
    }

    public void setNumberOfPage(int numberOfPage) {
        this.numberOfPage = numberOfPage;
    }
}
