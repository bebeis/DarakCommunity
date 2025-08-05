package darak.community.service.comment;

import darak.community.domain.comment.Comment;
import darak.community.domain.comment.CommentRepository;
import darak.community.domain.heart.CommentHeart;
import darak.community.domain.heart.CommentHeartRepository;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberRepository;
import darak.community.service.comment.response.MyCommentHeartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentHeartServiceImpl implements CommentHeartService {

    private final CommentHeartRepository commentHeartRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MyCommentHeartResponse addLike(Long commentId, Long memberId) {
        Comment comment = findCommentBy(commentId);
        Member member = findMemberBy(memberId);

        validateLikeNotExists(commentId, memberId);

        CommentHeart commentHeart = new CommentHeart(comment, member);
        commentHeartRepository.save(commentHeart);

        int likeCount = heartCountInComment(commentId);
        return new MyCommentHeartResponse(true, likeCount);
    }

    @Override
    @Transactional
    public MyCommentHeartResponse removeLike(Long commentId, Long memberId) {
        CommentHeart commentHeart = findCommentHeartBy(commentId, memberId);
        commentHeartRepository.delete(commentHeart);

        int likeCount = heartCountInComment(commentId);
        return new MyCommentHeartResponse(false, likeCount);
    }

    @Override
    public MyCommentHeartResponse getLikeStatus(Long commentId, Long memberId) {
        boolean isLiked = isLiked(commentId, memberId);
        int likeCount = heartCountInComment(commentId);
        return new MyCommentHeartResponse(isLiked, likeCount);
    }

    @Override
    public int heartCountInComment(Long commentId) {
        return commentHeartRepository.countByCommentId(commentId);
    }

    @Override
    public boolean isLiked(Long commentId, Long memberId) {
        return commentHeartRepository.findByCommentIdAndMemberId(commentId, memberId).isPresent();
    }

    private void validateLikeNotExists(Long commentId, Long memberId) {
        if (commentHeartRepository.findByCommentIdAndMemberId(commentId, memberId).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 댓글입니다.");
        }
    }

    private Comment findCommentBy(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private CommentHeart findCommentHeartBy(Long commentId, Long memberId) {
        return commentHeartRepository.findByCommentIdAndMemberId(commentId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글에 좋아요를 누르지 않았습니다."));
    }
}
