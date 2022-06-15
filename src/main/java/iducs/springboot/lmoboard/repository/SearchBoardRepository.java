package iducs.springboot.lmoboard.repository;

import iducs.springboot.lmoboard.domain.BoardStatus;
import iducs.springboot.lmoboard.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchBoardRepository {
    void updateBoardStatusREJECT(Long bor_id);
    void updateBoardStatusREADABLE(Long bor_id);
    List<BoardEntity> findWithSeq(Long seq);
    Page<Object[]> searchPage(String type, String keyword, Pageable pageable);
}