package darak.community.infra.comment.query;

import static darak.community.domain.board.QBoard.board;
import static darak.community.domain.comment.QComment.comment;
import static darak.community.domain.heart.QCommentHeart.commentHeart;
import static darak.community.domain.member.QMember.member;
import static darak.community.domain.post.QPost.post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import darak.community.infra.comment.query.dto.CommentInPostDto;
import darak.community.infra.comment.query.dto.CommentWithMetaDto;
import darak.community.service.comment.request.CommentSearch;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class QuerydslCommentQueryRepository implements CommentQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    public QuerydslCommentQueryRepository(EntityManager em) {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdPaged(Long memberId, Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.comment.query.dto.CommentWithMetaDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    p.id, p.title, p.postType,
                    b.id, b.name, c.createdDate,
                    case when
                        (select count(ch) from CommentHeart ch where ch.comment.id = c.id and ch.member.id = :memberId) > 0
                        then true else false end,
                    cast((select count(ch) from CommentHeart ch where ch.comment.id = c.id) as int)
                )
                from Comment c
                join c.member m
                join c.post p
                join p.board b
                where c.member.id = :memberId
                order by c.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from Comment c
                where c.member.id = :memberId
                """;

        List<CommentWithMetaDto> comments = em.createQuery(jpql, CommentWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberLiked(Long memberId, Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.comment.query.dto.CommentWithMetaDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    p.id, p.title, p.postType,
                    b.id, b.name, c.createdDate,
                    true,
                    cast((select count(ch2) from CommentHeart ch2 where ch2.comment.id = c.id) as int)
                )
                from CommentHeart ch
                join ch.comment c
                join c.member m
                join c.post p
                join p.board b
                where ch.member.id = :memberId
                order by ch.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from CommentHeart ch
                join ch.comment c
                where ch.member.id = :memberId
                """;

        List<CommentWithMetaDto> comments = em.createQuery(jpql, CommentWithMetaDto.class)
                .setParameter("memberId", memberId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    @Override
    public Page<CommentInPostDto> findCommentInPostByPostIdAndMemberIdPaged(Long postId, Long memberId,
                                                                            Pageable pageable) {
        // 기존 JPA 구현 유지
        String jpql = """
                select new darak.community.infra.comment.query.dto.CommentInPostDto(
                    c.id, m.id, c.anonymous, m.name, c.content,
                    case when c.parent is not null then true else false end,
                    c.createdDate,
                    case when
                        (select count(ch) from CommentHeart ch where ch.comment.id = c.id and ch.member.id = :memberId) > 0
                        then true else false end,
                    cast((select count(ch) from CommentHeart ch where ch.comment.id = c.id) as int)
                )
                from Comment c
                join c.member m
                where c.post.id = :postId
                order by c.createdDate desc
                """;

        String countJpql = """
                select count(c)
                from Comment c
                where c.post.id = :postId
                """;

        List<CommentInPostDto> comments = em.createQuery(jpql, CommentInPostDto.class)
                .setParameter("memberId", memberId)
                .setParameter("postId", postId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long count = em.createQuery(countJpql, Long.class)
                .setParameter("postId", postId)
                .getSingleResult();

        return new PageImpl<>(comments, pageable, count);
    }

    @Override
    public Page<CommentWithMetaDto> searchCommentsWithMetaByMemberIdAnd(Long memberId, CommentSearch commentSearch,
                                                                        Pageable pageable) {
        List<CommentWithMetaDto> content = queryFactory
                .select(Projections.constructor(CommentWithMetaDto.class,
                        comment.id,
                        member.id,
                        comment.anonymous,
                        member.name,
                        comment.content,
                        new CaseBuilder()
                                .when(comment.parent.isNotNull())
                                .then(true)
                                .otherwise(false),
                        post.id,
                        post.title,
                        post.postType,
                        board.id,
                        board.name,
                        comment.createdDate,
                        new CaseBuilder()
                                .when(JPAExpressions
                                        .select(commentHeart.count())
                                        .from(commentHeart)
                                        .where(commentHeart.comment.eq(comment)
                                                .and(commentHeart.member.id.eq(memberId)))
                                        .gt(0L))
                                .then(true)
                                .otherwise(false),
                        JPAExpressions
                                .select(commentHeart.count().intValue())
                                .from(commentHeart)
                                .where(commentHeart.comment.eq(comment))
                ))
                .from(comment)
                .join(comment.member, member)
                .join(comment.post, post)
                .join(post.board, board)
                .where(
                        comment.member.id.eq(memberId),
                        keywordContains(commentSearch.getKeyword()),
                        boardNameContains(commentSearch.getBoardName())
                )
                .orderBy(comment.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(comment.count())
                .from(comment)
                .join(comment.post, post)
                .join(post.board, board)
                .where(
                        comment.member.id.eq(memberId),
                        keywordContains(commentSearch.getKeyword()),
                        boardNameContains(commentSearch.getBoardName())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private BooleanExpression keywordContains(String keyword) {
        return StringUtils.hasText(keyword) ? comment.content.contains(keyword) : null;
    }

    private BooleanExpression boardNameContains(String boardName) {
        return StringUtils.hasText(boardName) ? board.name.contains(boardName) : null;
    }
} 