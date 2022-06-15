package iducs.springboot.lmoboard.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import iducs.springboot.lmoboard.domain.BoardStatus;
import iducs.springboot.lmoboard.entity.BoardEntity;
import iducs.springboot.lmoboard.entity.QBoardEntity;
import iducs.springboot.lmoboard.entity.QMemberEntity;
import iducs.springboot.lmoboard.entity.QReplyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Qualifier("SearchBoardRepositoryImpl")
@Slf4j
public class SearchBoardRepositoryImpl extends QuerydslRepositorySupport implements SearchBoardRepository {
    @PersistenceContext
    private EntityManager entityManager;

    private QBoardEntity boardEntity = QBoardEntity.boardEntity;
    private QReplyEntity replyEntity = QReplyEntity.replyEntity;
    private QMemberEntity memberEntity = QMemberEntity.memberEntity;

    public SearchBoardRepositoryImpl() {
        super(BoardEntity.class);
    }

    @Transactional
    @Override
    public void updateBoardStatusREJECT(Long bor_id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.update(boardEntity).where(boardEntity.bor_id.eq(bor_id))
                    .set(boardEntity.status, BoardStatus.REJECT)
                    .execute();
    }

    @Override
    public void updateBoardStatusREADABLE(Long bor_id) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        queryFactory.update(boardEntity).where(boardEntity.bor_id.eq(bor_id))
                .set(boardEntity.status, BoardStatus.READABLE)
                .execute();
    }

    @Override
    public List<BoardEntity> findWithSeq(Long seq) {
        JPQLQuery<BoardEntity> jpqlQuery = from(boardEntity);
        jpqlQuery.where(boardEntity.writer.seq.eq(seq));

        List<BoardEntity> result = jpqlQuery.fetch();

        log.info("test log of boardEntity search seq : {}", result);
        return result;
    }

    @Override
    public Page<Object[]> searchPage(String type, String keyword, Pageable pageable) {
        JPQLQuery<Tuple> tuple = jpqlBuilder();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        BooleanExpression expression = boardEntity.bor_id.gt(0L);

        booleanBuilder.and(expression);
        log.info("1 result of page : {}", tuple.fetch());

        if (type != null) {
            String[] typeArr = type.split("");
            BooleanBuilder conditionBuilder = new BooleanBuilder();

            for(String t : typeArr) {
                switch (t) {
                    case "t":
                        conditionBuilder.or(boardEntity.title.contains(keyword));
                        break;
                    case "w":
                        conditionBuilder.or(memberEntity.email.contains(keyword));
                        break;
                    case "c":
                        conditionBuilder.or(boardEntity.content.contains(keyword));
                }
            }
            booleanBuilder.and(conditionBuilder);
        }
        tuple.where(booleanBuilder);
        log.info("2 result of page : {}", tuple.fetch());

        Sort sort = pageable.getSort();
        sort.stream().forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC: Order.DESC;
            String prop = order.getProperty();
            PathBuilder orderByExpression = new PathBuilder(BoardEntity.class, "boardEntity");
            tuple.orderBy(new OrderSpecifier(direction, orderByExpression.get(prop)));
        });
        log.info("3 result of page : {}", tuple.fetch());

        tuple.groupBy(boardEntity);

        tuple.offset((pageable.getOffset()));
        tuple.limit(pageable.getPageSize());

        List<Tuple> result = tuple.fetch();

        log.info("last result of page : {}", tuple.fetch());
        log.info("result of page : {}", result);
        long count = tuple.fetchCount();

        return new PageImpl<Object[]>(result.stream().map(t -> t.toArray()).collect(Collectors.toList()), pageable, count);
    }

    public JPQLQuery<Tuple> jpqlBuilder() {
        JPQLQuery<BoardEntity> jpqlQuery = from(boardEntity);
        jpqlQuery.leftJoin(memberEntity).on(boardEntity.writer.eq(memberEntity));
        jpqlQuery.leftJoin(replyEntity).on(replyEntity.boardEntity.eq(boardEntity));
        return jpqlQuery.select(boardEntity, memberEntity, replyEntity.count());
    }
}
