package darak.community.service.comment;

import darak.community.domain.comment.Comment;
import darak.community.domain.member.Member;
import darak.community.domain.post.Post;
import darak.community.infra.adaptor.AdminLogRepositoryAdaptor;
import darak.community.infra.adaptor.CommentHeartRepositoryAdaptor;
import darak.community.infra.adaptor.CommentRepositoryAdaptor;
import darak.community.infra.adaptor.MemberRepositoryAdaptor;
import darak.community.infra.adaptor.PostRepositoryAdaptor;
import darak.community.infra.adaptor.dto.CommentInPostDto;
import darak.community.infra.adaptor.dto.CommentWithMetaDto;
import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.CommentSearch;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final MemberRepositoryAdaptor memberRepository;
    private final CommentRepositoryAdaptor commentRepository;
    private final PostRepositoryAdaptor postRepository;
    private final AdminLogRepositoryAdaptor adminLogRepository;
    private final CommentHeartRepositoryAdaptor commentHeartRepository;
    private final CommentHeartService commentHeartService; // 추가

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
    public Page<CommentInPostDto> findCommentsInPostBy(Long memberId, Long postId, Pageable pageable) {
        return commentRepository.findCommentInPostByPostIdAndMemberIdPaged(
                postId, memberId, pageable);
    }

    @Override
    public Page<CommentWithMetaDto> searchCommentsWithMetaByMemberIdAnd(Long memberId, CommentSearch commentSearch) {
        Pageable pageable = PageRequest.of(commentSearch.getPage(), commentSearch.getSize());
        Page<CommentWithMetaDto> comments = commentRepository.findCommentsWithMetaByMemberIdPaged(
                memberId, pageable);

        if (commentSearch.getBoardName() == null || commentSearch.getBoardName().isEmpty()) {
            return comments;
        }

        List<CommentWithMetaDto> filteredComments = comments.stream()
                .filter(comment -> comment.getBoardName().contains(commentSearch.getBoardName())
                        || comment.getContent().contains(commentSearch.getKeyword()))
                .toList();

        return new PageImpl<>(filteredComments, pageable, filteredComments.size());
    }

    @Override
    public Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdAndHearted(Long memberId, Pageable pageable) {
        return commentRepository.findCommentsWithMetaByMemberLiked(memberId, pageable);
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
