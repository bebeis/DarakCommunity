package darak.community.domain.heart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PostHeartRepository {
    void save(PostHeart postHeart);
    
    void delete(PostHeart postHeart);
    
    int countByPostId(Long postId);
    
    List<PostHeart> findByMemberId(Long memberId);
    
    Optional<PostHeart> findByPostIdAndMemberId(Long postId, Long memberId);
    
    Page<PostHeart> findByMemberIdFetchPost(Long memberId, Pageable pageable);
    
    List<PostHeart> findByPostId(Long postId);
}
