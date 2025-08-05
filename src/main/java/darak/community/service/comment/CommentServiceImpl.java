package darak.community.service.comment;

import darak.community.domain.comment.Comment;
import darak.community.domain.comment.CommentRepository;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberRepository;
import darak.community.domain.post.Post;
import darak.community.domain.post.PostRepository;
import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public void createFromPost(CommentCreateServiceRequest request) {
        Comment comment = Comment.createComment(
                request.getContent(),
                request.isAnonymous(),
                findPostBy(request.getPostId()),
                findMemberBy(request.getMemberId()));

        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void createReplyFromPost(ReplyCreateServiceRequest request) {
        Comment reply = Comment.createReply(
                request.getContent(),
                request.isAnonymous(),
                findPostBy(request.getPostId()),
                findMemberBy(request.getMemberId()),
                findParentCommentBy(request.getParentCommentId())
        );

        commentRepository.save(reply);
    }

    @Override
    @Transactional
    public void deleteCommentBy(Long memberId, Long commentId) {
        Comment comment = findParentCommentBy(commentId);
        Member member = findMemberBy(memberId);
        validateAuthor(member, comment);
        commentRepository.delete(comment);
    }

    @Override
    public long getTotalCommentCount() {
        return commentRepository.count();
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));
    }

    private Comment findParentCommentBy(Long parentCommentId) {
        return commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
    }

    private void validateAuthor(Member member, Comment comment) {
        if (!isCommentAuthor(comment, member)) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
    }

    private boolean isCommentAuthor(Comment comment, Member member) {
        return comment.getMember().equals(member);
    }

}
