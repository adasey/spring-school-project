package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Board;

import java.util.List;

public interface BoardService {
    Long register(Board dto);
    Long modifyById(Board dto);
    Board getById(Long bor_id);
    List<Long> findAllWriterSeq(Long seq);
    void updateRejectWriterByBoard(List<Long> ids);
    void updateReadableWriterByBoard(List<Long> ids);
    void updateBoard(Board dto);
    void deleteWithRepliesById(Long bor_id); // with : join 관계된 값들을 전부 삭제함.
}
