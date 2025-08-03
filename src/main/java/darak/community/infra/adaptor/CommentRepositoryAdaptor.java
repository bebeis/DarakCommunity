package darak.community.infra.adaptor;

import darak.community.domain.comment.Comment;
import darak.community.domain.comment.CommentRepository;
import darak.community.infra.adaptor.dto.CommentInPostDto;
import darak.community.infra.adaptor.dto.CommentWithMetaDto;
import darak.community.infra.repository.CommentJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdaptor implements CommentRepository {

    private final EntityManager em;
    private final CommentJpaRepository repository;

    @Override
    public void save(Comment comment) {
        repository.save(comment);
    }

    @Override
    public void delete(Comment comment) {
        repository.delete(comment);
    }

    @Override
    public Optional<Comment> findById(Long commentId) {
        return repository.findById(commentId);
    }

    // TODO: queryDsl로 작성
    // Spring Data Jpa 카운트 시, count 쿼리에서 조인 발생
    @Override
    public long countByMemberId(Long memberId) {
        return em.createQuery("select count(c) from Comment c where c.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();

    }

    // TODO: queryDsl로 작성
    // Spring Data Jpa 카운트 시, count 쿼리에서 조인 발생
    @Override
    public long countLikesByMemberId(Long memberId) {
        return em.createQuery(
                        "select count(ch) from CommentHeart ch " +
                                "join ch.comment c " +
                                "where c.member.id = :memberId", Long.class)
                .setParameter("memberId", memberId)
                .getSingleResult();
    }

    @Override
    public long count() {
        return em.createQuery("select count(c) from Comment c", Long.class)
                .getSingleResult();
    }

    // TODO: queryDsl
    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdPaged(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.CommentWithMetaDto(
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

    // TODO: queryDsl
    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberLiked(Long memberId, Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.CommentWithMetaDto(
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

    // TODO: queryDsl
    @Override
    public Page<CommentInPostDto> findCommentInPostByPostIdAndMemberIdPaged(Long postId, Long memberId,
                                                                            Pageable pageable) {
        String jpql = """
                select new darak.community.infra.adaptor.dto.CommentInPostDto(
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
}
