package darak.community.infra.post.query;

import static darak.community.domain.board.QBoard.board;
import static darak.community.domain.comment.QComment.comment;
import static darak.community.domain.heart.QPostHeart.postHeart;
import static darak.community.domain.member.QMember.member;
import static darak.community.domain.post.QPost.post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import darak.community.infra.post.query.dto.PostContentDto;
import darak.community.infra.post.query.dto.PostWithAllDto;
import darak.community.service.post.request.PostSearch;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class QuerydslPostQueryRepository implements PostQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public QuerydslPostQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostWithAllDto> findPostsWithMetaByBoardId(Long boardId, Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.post.query.dto.PostWithAllDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c
                             where c.post = p) as int),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph
                             where ph.post = p) as int)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        where b.id = :boardId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.board.id = :boardId
                """;

        List<PostWithAllDto> content = em.createQuery(jpql, PostWithAllDto.class)
                .setParameter("boardId", boardId)
                .setParameter("memberId", pageable.getPageNumber())
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostWithAllDto> findPostsWithMetaWrittenByMemberId(Long memberId, Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.post.query.dto.PostWithAllDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c
                             where c.post = p) as int),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph
                             where ph.post = p) as int)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        where p.member.id = :memberId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.member.id = :memberId
                """;

        List<PostWithAllDto> content = em.createQuery(jpql, PostWithAllDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostWithAllDto> findPostsWithMetaByMemberLiked(Long memberId, Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.post.query.dto.PostWithAllDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade, b.id, b.name,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c
                             where c.post = p) as int),
                            case when
                                 (select count(ph2) from PostHeart ph2
                                  where ph2.post = p
                                    and ph2.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph
                             where ph.post = p) as int)
                        )
                        from Post p
                        join p.member m
                        join p.board b
                        join PostHeart ph on ph.post.id = p.id
                        where ph.member.id = :memberId
                        order by p.createdDate desc
                """;
        String countJpql = """
                select count(p)
                from Post p
                where p.member.id = :memberId
                """;

        List<PostWithAllDto> content = em.createQuery(jpql, PostWithAllDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<PostContentDto> findPostContentByMemberIdAndPostId(Long postId, Long memberId) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.post.query.dto.PostContentDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c where c.post = p) as int),
                            case when
                                 (select count(ph) from PostHeart ph where ph.post.id = p.id and ph.member.id = :memberId) > 0
                                 then true
                                 else false
                             end,
                            cast((select count(ph) from PostHeart ph where ph.post.id = p.id) as int)
                        )
                        from Post p
                        join p.member m
                        where p.id = :postId
                """;

        return Optional.ofNullable(em.createQuery(jpql, PostContentDto.class)
                .setParameter("postId", postId)
                .setParameter("memberId", memberId)
                .getSingleResult());
    }

    @Override
    public Page<PostContentDto> findPostsByBoardId(Long boardId, Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.post.query.dto.PostContentDto(
                            p.id, p.title, p.content, p.anonymous, p.postType,
                            m.id, m.name, m.memberGrade,
                            p.readCount, p.createdDate,
                            cast((select count(c) from Comment c where c.post = p) as int),
                            false,
                            cast((select count(ph) from PostHeart ph where ph.post.id = p.id) as int)
                        )
                        from Post p
                        join p.member m
                        where p.board.id = :boardId
                        order by p.createdDate desc
                """;

        List<PostContentDto> content = em.createQuery(jpql, PostContentDto.class)
                .setParameter("boardId", boardId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = em.createQuery("select count(p) from Post p where p.board.id = :boardId", Long.class)
                .setParameter("boardId", boardId)
                .getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<PostWithAllDto> searchPostsWithMetaByMemberIdAnd(Long memberId, PostSearch postSearch,
                                                                 Pageable pageable) {
        List<PostWithAllDto> content = queryFactory
                .select(Projections.constructor(PostWithAllDto.class,
                        post.id,
                        post.title,
                        post.content,
                        post.anonymous,
                        post.postType,
                        member.id,
                        member.name,
                        member.memberGrade,
                        board.id,
                        board.name,
                        post.readCount,
                        post.createdDate,
                        JPAExpressions
                                .select(comment.count().intValue())
                                .from(comment)
                                .where(comment.post.eq(post)),
                        new CaseBuilder()
                                .when(JPAExpressions
                                        .select(postHeart.count())
                                        .from(postHeart)
                                        .where(postHeart.post.eq(post)
                                                .and(postHeart.member.id.eq(memberId)))
                                        .gt(0L))
                                .then(true)
                                .otherwise(false),
                        JPAExpressions
                                .select(postHeart.count().intValue())
                                .from(postHeart)
                                .where(postHeart.post.eq(post))
                ))
                .from(post)
                .join(post.member, member)
                .join(post.board, board)
                .where(
                        post.member.id.eq(memberId),
                        keywordContains(postSearch.getKeyword()),
                        boardNameContains(postSearch.getBoardName())
                )
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .join(post.board, board)
                .where(
                        post.member.id.eq(memberId),
                        keywordContains(postSearch.getKeyword()),
                        boardNameContains(postSearch.getBoardName())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? post.title.contains(keyword) : null;
    }

    private BooleanExpression boardNameContains(String boardName) {
        return StringUtils.hasText(boardName) ? board.name.contains(boardName) : null;
    }
} 