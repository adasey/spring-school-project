package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Member;
import iducs.springboot.lmoboard.domain.PageRequestDTO;
import iducs.springboot.lmoboard.domain.PageResultDTO;
import iducs.springboot.lmoboard.entity.MemberEntity;

import java.util.List;

public interface MemberService {
    void create(Member member);

    Member readById(Long seq);
    List<Member> readAll();
    PageResultDTO<Member, MemberEntity> readListBy(PageRequestDTO pageRequestDTO);

    void update(Member member);
    void delete(Member member);

    void rejectUserBoard(Member member);
    void memberUserBoard(Member member);
}
