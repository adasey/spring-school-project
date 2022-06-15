package iducs.springboot.lmoboard.service;

import iducs.springboot.lmoboard.domain.Member;
import iducs.springboot.lmoboard.entity.MemberEntity;

public interface MemberConversionService {
    Member entityToDto(MemberEntity entity);
    MemberEntity dtoToEntity(Member member);
}
