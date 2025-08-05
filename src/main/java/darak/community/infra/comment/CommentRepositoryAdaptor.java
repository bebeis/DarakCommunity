package darak.community.infra.comment;

import darak.community.domain.comment.Comment;
import darak.community.domain.comment.CommentRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
