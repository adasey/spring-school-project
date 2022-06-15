package iducs.springboot.lmoboard.service;

import com.querydsl.core.BooleanBuilder;
import iducs.springboot.lmoboard.domain.Member;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.domain.PageResultDTO;
import iducs.springboot.lmoboard.entity.MemberEntity;

public interface MemberPageService {
    PageResultDTO<Member, MemberEntity> readListBy(PageRequestDTO pageRequestDTO);
    BooleanBuilder findByCondition(PageRequestDTO pageRequestDTO);
}
