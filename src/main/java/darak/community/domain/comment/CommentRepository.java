package darak.community.domain.comment;

import darak.community.infra.adaptor.dto.CommentInPostDto;
import darak.community.infra.adaptor.dto.CommentWithMetaDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository {
    void save(Comment comment);

    void delete(Comment comment);

    Optional<Comment> findById(Long commentId);

    long countByMemberId(Long memberId);

    long countLikesByMemberId(Long memberId);

    long count();

    Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdPaged(Long memberId, Pageable pageable);

    Page<CommentWithMetaDto> findCommentsWithMetaByMemberLiked(Long memberId, Pageable pageable);

    Page<CommentInPostDto> findCommentInPostByPostIdAndMemberIdPaged(Long postId, Long memberId, Pageable pageable);
}
