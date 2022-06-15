package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Board;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.domain.PageResultDTO;
import iducs.springboot.lmoboard.entity.BoardEntity;
import iducs.springboot.lmoboard.entity.MemberEntity;
import iducs.springboot.lmoboard.entity.QBoardEntity;
import iducs.springboot.lmoboard.repository.BoardRepository;
import iducs.springboot.lmoboard.repository.ReplyRepository;
import iducs.springboot.lmoboard.repository.SearchBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Log4j2
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService, BoardPageService, BoardConversionService {
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final SearchBoardRepository searchBoardRepository;

    private QBoardEntity boardEntity = QBoardEntity.boardEntity;

    @Override
    public Long register(Board dto) {
        log.info("board register : {}" + dto);
        BoardEntity boardEntity = dtoToEntity(dto);
        boardRepository.save(boardEntity);
        return boardEntity.getBor_id();
    }

    @Override
    public Long modifyById(Board dto) {
        Optional<BoardEntity> result = boardRepository.findById(dto.getBor_id());
        BoardEntity boardEntity = null;

        if (result.isPresent()) {
            boardEntity = (BoardEntity) result.get();
            boardEntity.changeTitle(dto.getTitle());
            boardEntity.changeContent(dto.getContent());
            boardRepository.save(boardEntity);
        }

        return boardEntity.getBor_id();
    }

    @Override
    public Board getById(Long bor_id) {
        Object result = boardRepository.getBoardByBor_id(bor_id);
        Object[] resultEntity = (Object[]) result;

        return entityToDto((BoardEntity) resultEntity[0], (MemberEntity) resultEntity[1], (Long) resultEntity[2]);
    }

    @Override
    public List<Long> findAllWriterSeq(Long seq) {
        List<BoardEntity> boardBySeq = searchBoardRepository.findWithSeq(seq);

        log.info("test log of findAllWriter search seq : {}", boardBySeq);

        List<Long> results = new ArrayList<>();
        for (BoardEntity entity: boardBySeq) {
            results.add(entity.getBor_id());
        }

        return results;
    }

    @Override
    public void updateRejectWriterByBoard(List<Long> ids) {
        for (long id : ids) {
            searchBoardRepository.updateBoardStatusREJECT(id);
        }
    }

    @Override
    public void updateReadableWriterByBoard(List<Long> ids) {
        for (long id : ids) {
            searchBoardRepository.updateBoardStatusREADABLE(id);
        }
    }

    @Override
    public void updateBoard(Board dto) {
        BoardEntity entity = null;
        entity = dtoToEntity(dto);
        boardRepository.save(entity);
    }

    @Transactional
    @Override
    public void deleteWithRepliesById(Long bor_id) {
        replyRepository.deleteByBor_id(bor_id);
        boardRepository.deleteById(bor_id);
    }

    @Override
    public PageResultDTO<Board, Object[]> getList(PageRequestDTO pageRequestDTO) {
        Function<Object[], Board> fn = (entities -> entityToDto((BoardEntity) entities[0], (MemberEntity) entities[1], (Long) entities[2]));
        log.info("get test entities : {}", fn);
        Sort sort = pageRequestDTO.getOrder() == 0 ? Sort.by("bor_id").descending() : Sort.by("bor_id").ascending();
        Page<Object[]> result = searchBoardRepository.searchPage(pageRequestDTO.getType(), pageRequestDTO.getKeyword(), pageRequestDTO.getPageable(sort));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public Board entityToDto(BoardEntity entity) {
        Board dto = Board.builder()
                .bor_id(entity.getBor_id())
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(entity.getStatus())
                .build();

        return dto;
    }

    @Override
    public BoardEntity dtoToEntity(Board dto) {
        MemberEntity memberEntity = MemberEntity.builder()
                .seq(dto.getWriterSeq())
                .build();

        BoardEntity entity = BoardEntity.builder()
                .bor_id(dto.getBor_id())
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(dto.getStatus())
                .writer(memberEntity)
                .build();

        return entity;
    }

    @Override
    public Board entityToDto(BoardEntity entity, MemberEntity member, Long replyCount) {
        Board dto = Board.builder()
                .bor_id(entity.getBor_id())
                .title(entity.getTitle())
                .content(entity.getContent())
                .status(entity.getStatus())
                .writerSeq(member.getSeq())
                .writerId(member.getId())
                .writerName(member.getName())
                .writerEmail(member.getEmail())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .replyCount(replyCount.intValue())
                .build();

        return dto;
    }
}
