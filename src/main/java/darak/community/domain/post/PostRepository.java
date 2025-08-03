package darak.community.domain.post;

import darak.community.infra.adaptor.dto.PostContentDto;
import darak.community.infra.adaptor.dto.PostWithAllDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepository {
    void save(Post post);

    Optional<Post> findById(Long id);

    void delete(Post post);

    List<Attachment> findRecentGalleryImages(int limit);

    Page<Post> findByBoardIdPaged(Long boardId, Pageable pageable);

    List<Post> findRecentGalleryPostsWithImages(int limit);

    long countByMemberId(Long memberId);

    long countLikesByMemberId(Long memberId);

    Page<PostWithAllDto> findPostsWithMetaByBoardId(Long boardId, Pageable pageable);

    Page<PostWithAllDto> findPostsWithMetaWrittenByMemberId(Long memberId, Pageable pageable);

    Page<PostWithAllDto> findPostsWithMetaByMemberLiked(Long memberId, Pageable pageable);

    long count();

    Optional<PostContentDto> findPostContentByMemberIdAndPostId(Long postId, Long memberId);

    Page<PostContentDto> findPostsByBoardId(Long boardId, Pageable pageable);
}
