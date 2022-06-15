package iducs.springboot.lmoboard.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import iducs.springboot.lmoboard.domain.*;
import iducs.springboot.lmoboard.entity.MemberEntity;
import iducs.springboot.lmoboard.entity.QMemberEntity;
import iducs.springboot.lmoboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService, MemberPageService, MemberLoginService, MemberConversionService {
    private final MemberRepository memberRepository;
    private final BoardService boardService;

    @Override
    public void create(Member member) {
        // .seq(member.getSeq())
        MemberEntity entity = dtoToEntity(member);

        memberRepository.save(entity);
    }

    @Override
    public Member readById(Long seq) {
        Member member = null;

        Optional<MemberEntity> result = memberRepository.findById(seq);

        if (result.isPresent()) {
            member = entityToDto(result.get());
        }

        return member;
    }

    @Override
    public List<Member> readAll() {
        List<Member> members = new ArrayList<>();
        List<MemberEntity> entities = memberRepository.findAll();

        for (MemberEntity entity : entities) {
            Member member = entityToDto(entity);
            members.add(member);
        }

        return members;
    }

    @Override
    public PageResultDTO<Member, MemberEntity> readListBy(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getOrder() == 0 ? Sort.by("seq").descending() : Sort.by("seq").ascending();
        Pageable pageable = pageRequestDTO.getPageable(sort);

        BooleanBuilder booleanBuilder = findByCondition(pageRequestDTO);
        log.info("boolean check : {}", booleanBuilder);

        Page<MemberEntity> result = memberRepository.findAll(booleanBuilder, pageable);
        Function<MemberEntity, Member> fn = (entity -> entityToDto(entity));

        return new PageResultDTO<>(result, fn);
    }

    @Override
    public BooleanBuilder findByCondition(PageRequestDTO pageRequestDTO) {
        String type = pageRequestDTO.getType();
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QMemberEntity qMemberEntity = QMemberEntity.memberEntity;

        BooleanExpression expression = qMemberEntity.seq.gt(0L);
        booleanBuilder.and(expression);

        if(type == null || type.trim().length() == 0) {
            return booleanBuilder;
        }
        String keyword = pageRequestDTO.getKeyword();

        BooleanBuilder conditionBuilder = new BooleanBuilder();
        if(type.contains("e")) // email로 검색
            conditionBuilder.or(qMemberEntity.email.contains(keyword));
        if(type.contains("p")) // phone로 검색
            conditionBuilder.or(qMemberEntity.phone.contains(keyword));
        if(type.contains("a")) // address로 검색
            conditionBuilder.or(qMemberEntity.address.contains(keyword));

        booleanBuilder.and(conditionBuilder);
        return booleanBuilder;
    }

    @Override
    public void update(Member member) {
        MemberEntity entity = dtoToEntity(member);

        memberRepository.save(entity);
    }

    @Override
    public void delete(Member member) {
        MemberEntity entity = dtoToEntity(member);
        memberRepository.deleteById(entity.getSeq());
    }

    @Override
    public void rejectUserBoard(Member member) {
        if (member.getStatus().equals(Status.REJECT)) {
            List<Long> results = boardService.findAllWriterSeq(member.getSeq());

            boardService.updateRejectWriterByBoard(results);
        }
    }

    @Override
    public void memberUserBoard(Member member) {
        if (member.getStatus().equals(Status.MEMBER)) {
            List<Long> results = boardService.findAllWriterSeq(member.getSeq());

            boardService.updateRejectWriterByBoard(results);
        }
    }

    @Override
    public Member entityToDto(MemberEntity entity) {
        Member dto = Member.builder()
                .seq(entity.getSeq())
                .id(entity.getId())
                .pw(entity.getPw())
                .name(entity.getName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .build();

        return dto;
    }

    @Override
    public MemberEntity dtoToEntity(Member member) {
        MemberEntity entity = MemberEntity.builder()
                .seq(member.getSeq())
                .id(member.getId())
                .pw(member.getPw())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .address(member.getAddress())
                .status(member.getStatus())
                .build();

        return entity;
    }

    @Override
    public Member loginByEmail(Member member) {
        Member dto = null;
        Object result = memberRepository.getMemberByEmail(member.getEmail(), member.getPw());
        if (result != null) {
            dto = entityToDto((MemberEntity) result);
        }

        return dto;
    }
}
