package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Member;
import org.springframework.stereotype.Service;

@Service
public interface MemberLoginService {
    Member loginByEmail(Member member);
}
