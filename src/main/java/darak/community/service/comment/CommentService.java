package darak.community.service.comment;

import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.ReplyCreateServiceRequest;

public interface CommentService {

    void createFromPost(CommentCreateServiceRequest request);

    void createReplyFromPost(ReplyCreateServiceRequest request);

    void deleteCommentBy(Long commentId, Long memberId);

    long getTotalCommentCount();
}
