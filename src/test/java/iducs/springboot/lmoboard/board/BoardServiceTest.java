package iducs.springboot.lmoboard.board;

import iducs.springboot.lmoboard.domain.Board;
import iducs.springboot.lmoboard.domain.BoardStatus;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.domain.PageResultDTO;
import iducs.springboot.lmoboard.entity.BoardEntity;
import iducs.springboot.lmoboard.entity.MemberEntity;
import iducs.springboot.lmoboard.repository.BoardRepository;
import iducs.springboot.lmoboard.service.BoardConversionService;
import iducs.springboot.lmoboard.service.BoardPageService;
import iducs.springboot.lmoboard.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class BoardServiceTest {

    @Autowired
    BoardService boardService;
    BoardRepository boardRepository;
    BoardPageService boardPageService;
    BoardConversionService boardConversionService;

    @Test
    public void testRegister() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Long seqLong = Long.valueOf(new Random().nextInt(51));
            seqLong = (seqLong <= 0) ? 1 : seqLong;
            Board dto = Board.builder()
                    .title("title" + i)
                    .content("content" + (seqLong + i))
                    .status(BoardStatus.READABLE)
                    .writerSeq(seqLong) // member entity의 seq 값 중 존재하는 값이여야 한다.
                    .build();

            Long bor_id = boardService.register(dto);
        });
    }

    @Test
    void testList() {
        PageRequestDTO pageRequestDTO = new PageRequestDTO();
        pageRequestDTO.setPage(3);
        pageRequestDTO.setSize(4);
        PageResultDTO<Board, Object[]> result = boardPageService.getList(pageRequestDTO);
        for (Board dto : result.getDtoList()) {
            System.out.println(dto.getBor_id() + ", " + dto.getTitle());
        }
        System.out.println("result = " + result);
        System.out.println("pageRequestDTO = " + pageRequestDTO);
    }

    @Transactional // spring transactional db에 대해 매번 새롭게 접근하게 해줌.
    @Test
    void testLazyLoading() {
        Optional<BoardEntity> result = boardRepository.findById(10L);
        BoardEntity boardEntity = result.get();
        System.out.println("Bor_id = " + boardEntity.getBor_id());
        System.out.println("Writer = " + boardEntity.getWriter());
    }

//    @Transactional
    @Test
    void testDeleteWithRepliesById() {
        Long bor_id = 3L;
        boardService.deleteWithRepliesById(bor_id);
    }

    @Test
    @Transactional
    void testUpdateBor_id() {
        Long bor_id = 1L;
        Object result1 = boardRepository.getBoardByBor_id(bor_id);
        Object[] resultEntity1 = (Object[]) result1;

        Board board1 = boardConversionService.entityToDto((BoardEntity) resultEntity1[0], (MemberEntity) resultEntity1[1], (Long) resultEntity1[2]);
        board1.setContent("test");
        board1.setTitle("test");
        
        boardService.updateBoard(board1);

        Object result2 = boardRepository.getBoardByBor_id(bor_id);
        Object[] resultEntity2 = (Object[]) result2;

        Board board2 = boardConversionService.entityToDto((BoardEntity) resultEntity2[0], (MemberEntity) resultEntity2[1], (Long) resultEntity2[2]);

        assertThat(board1.getTitle()).isSameAs(board2.getTitle());
        assertThat(board1.getContent()).isSameAs(board2.getContent());
        System.out.println("result1 = " + board1.getTitle());
        System.out.println("result2 = " + board2.getTitle());
    }

    @Test
    void boardSearchWithSeq() {
    }
}
