package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Board;
import iducs.springboot.lmoboard.entity.BoardEntity;
import iducs.springboot.lmoboard.entity.MemberEntity;

public interface BoardConversionService {
    Board entityToDto(BoardEntity entity);
    BoardEntity dtoToEntity(Board dto);
    Board entityToDto(BoardEntity entity, MemberEntity member, Long replyCount);
}
