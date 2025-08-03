package darak.community.service.comment;

import darak.community.infra.adaptor.dto.CommentInPostDto;
import darak.community.infra.adaptor.dto.CommentWithMetaDto;
import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.CommentSearch;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

    void createFromPost(CommentCreateServiceRequest request);

    void createReplyFromPost(ReplyCreateServiceRequest request);

    void deleteCommentBy(Long commentId, Long memberId);

    Page<CommentInPostDto> findCommentsInPostBy(Long memberId, Long postId, Pageable pageable);

    Page<CommentWithMetaDto> searchCommentsWithMetaByMemberIdAnd(Long memberId, CommentSearch commentSearch);

    Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdAndHearted(Long memberId, Pageable pageable);

    long getTotalCommentCount();
}
