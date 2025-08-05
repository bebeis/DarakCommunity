package darak.community.service.post;

import darak.community.domain.heart.PostHeart;
import darak.community.domain.heart.PostHeartRepository;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberRepository;
import darak.community.domain.post.Post;
import darak.community.domain.post.PostRepository;
import darak.community.service.post.response.MyPostHeartResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostHeartServiceImpl implements PostHeartService {

    private final PostHeartRepository postHeartRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public MyPostHeartResponse addLike(Long postId, Long memberId) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(memberId);

        validateLikeNotExists(postId, memberId);

        PostHeart postHeart = new PostHeart(post, member);
        postHeartRepository.save(postHeart);

        int likeCount = heartCountInPost(postId);
        return new MyPostHeartResponse(true, likeCount);
    }

    @Override
    @Transactional
    public MyPostHeartResponse removeLike(Long postId, Long memberId) {
        PostHeart postHeart = findPostHeartBy(postId, memberId);
        postHeartRepository.delete(postHeart);

        int likeCount = heartCountInPost(postId);
        return new MyPostHeartResponse(false, likeCount);
    }

    @Override
    public MyPostHeartResponse getLikeStatus(Long postId, Long memberId) {
        boolean isLiked = isLiked(postId, memberId);
        int likeCount = heartCountInPost(postId);
        return new MyPostHeartResponse(isLiked, likeCount);
    }

    @Override
    public List<PostHeart> findByMemberId(Long memberId) {
        return postHeartRepository.findByMemberId(memberId);
    }

    @Override
    public int heartCountInPost(Long postId) {
        return postHeartRepository.countByPostId(postId);
    }

    @Override
    public boolean isLiked(Long postId, Long memberId) {
        return postHeartRepository.findByPostIdAndMemberId(postId, memberId).isPresent();
    }

    private void validateLikeNotExists(Long postId, Long memberId) {
        if (postHeartRepository.findByPostIdAndMemberId(postId, memberId).isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 게시글입니다.");
        }
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private PostHeart findPostHeartBy(Long postId, Long memberId) {
        return postHeartRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글에 좋아요를 누르지 않았습니다."));
    }
}
