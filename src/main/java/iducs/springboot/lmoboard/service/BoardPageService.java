package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Board;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.domain.PageResultDTO;

public interface BoardPageService {
    PageResultDTO<Board, Object[]> getList(PageRequestDTO pageRequestDTO);
}
