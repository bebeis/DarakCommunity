package darak.community.domain.comment;

import java.util.Optional;

public interface CommentRepository {
    void save(Comment comment);

    void delete(Comment comment);

    Optional<Comment> findById(Long commentId);

    long countByMemberId(Long memberId);

    long countLikesByMemberId(Long memberId);

    long count();
}
