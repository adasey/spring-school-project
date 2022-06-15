package iducs.springboot.lmoboard.member;

import iducs.springboot.lmoboard.domain.Member;
import iducs.springboot.lmoboard.domain.Status;
import iducs.springboot.lmoboard.entity.MemberEntity;
import iducs.springboot.lmoboard.repository.MemberRepository;
import iducs.springboot.lmoboard.service.BoardService;
import iducs.springboot.lmoboard.service.MemberConversionService;
import iducs.springboot.lmoboard.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RequiredArgsConstructor
@Slf4j
public class MemberTest {
    @Autowired
    MemberService memberService;
    @Autowired
    MemberConversionService memberConService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BoardService boardService;

    public Member makeAdminUser() {
        Member member = Member.builder()
                .id("admin")
                .pw("admin")
                .name("admin")
                .email("admin@induk.ac.kr")
                .phone("00000000000")
                .address("admin")
                .status(Status.ADMIN)
                .build();

        return member;
    }

    public Member makeUser(int id) {
        Member member = Member.builder()
                .id("id" + id)
                .pw("pw" + id)
                .name("name" + id)
                .email("email" + id + "@induk.ac.kr")
                .phone("" + id * (10 * id))
                .address("address" + id)
                .status(Status.MEMBER)
                .build();

        return member;
    }

    public Member makeUserWithSeq(int id) {
        Member member = Member.builder()
                .seq(Long.valueOf(Integer.toString(id)))
                .id("id" + id)
                .pw("pw" + id)
                .name("name" + id)
                .email("email" + id + "@induk.ac.kr")
                .phone("" + id * (10 * id))
                .address("address" + id)
                .status(Status.MEMBER)
                .build();

        return member;
    }

    @Test
    void dbCreateTest() {
        Member dto = makeAdminUser();

        memberService.create(dto);
    }

    @Test
    @Transactional
    void makeSingleMemberTest() {
        Member test = makeUser(0);
        Member dto = makeUserWithSeq(0);

        log.info("made by function : {}", dto);

        memberService.create(test);
        Member member = memberService.readById(0L);
        Member member1 = memberService.readById(1L);

        log.info("read test : {}", memberService.readAll());
        log.info("made by readById : {}", member);

        assertThat(dto).isEqualTo(member);
        assertThat(dto).isEqualTo(member1);
    }

    @Test
    void memberCreateLotsTest() {
        IntStream.rangeClosed(1, 50).forEach(i -> {
            MemberEntity entity = memberConService.dtoToEntity(makeUser(i));

            memberRepository.save(entity);
        });

        assertThat(memberService.readAll().size()).isEqualTo(51);
    }

    @Test
    void memberRejectSaveRejectTest() {
        Member reject = memberService.readById(50L);

//        reject.setStatus(Status.REJECT);
//        memberService.update(reject);
        memberService.rejectUserBoard(reject);

        List<Long> resultLongs = boardService.findAllWriterSeq(reject.getSeq());
        for (Long id : resultLongs) {
            log.info("board of reject member's : {}", boardService.getById(id));
        }
    }

    @Test
    void memberSaveReadableTest() {
        Member member = memberService.readById(50L);

        member.setStatus(Status.MEMBER);
        memberService.update(member);
        memberService.memberUserBoard(member);

        List<Long> resultLongs = boardService.findAllWriterSeq(member.getSeq());
        for (Long id : resultLongs) {
            log.info("board of reject member's : {}", boardService.getById(id));
        }
    }

    @Test
    @Transactional
    void memberRejectTest() {
        Member reject = memberService.readById(50L);

        reject.setStatus(Status.REJECT);
        memberService.update(reject);
        memberService.rejectUserBoard(reject);

        List<Long> resultLongs = boardService.findAllWriterSeq(reject.getSeq());
        for (Long id : resultLongs) {
            log.info("board of reject member's : {}", boardService.getById(id));
        }
    }
}
