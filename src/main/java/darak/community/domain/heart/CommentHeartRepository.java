package darak.community.domain.heart;

import java.util.List;
import java.util.Optional;

public interface CommentHeartRepository {
    void save(CommentHeart commentHeart);

    void delete(CommentHeart commentHeart);

    List<CommentHeart> findByMemberId(Long memberId);

    List<CommentHeart> findByMemberIdFetchComments(Long memberId);

    Optional<CommentHeart> findByCommentIdAndMemberId(Long commentId, Long memberId);

    int countByCommentId(Long commentId);

}
