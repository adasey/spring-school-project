package iducs.springboot.lmoboard.repository;

import iducs.springboot.lmoboard.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long>, QuerydslPredicateExecutor<MemberEntity> {
    @Query(value="select m from MemberEntity m where m.email = :email and m.pw = :pw")
    Object getMemberByEmail(@Param("email") String email, @Param("pw") String pw);
}
