package iducs.springboot.lmoboard.repository;

import iducs.springboot.lmoboard.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    @Modifying
    @Query("delete from ReplyEntity r where r.boardEntity.bor_id = :bor_id")
    void deleteByBor_id(@Param("bor_id") Long bor_id);
}
