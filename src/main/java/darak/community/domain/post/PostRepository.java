package darak.community.domain.post;

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

    long count();
}
