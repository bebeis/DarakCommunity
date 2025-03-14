package darak.community.service;

import darak.community.domain.Attachment;
import darak.community.domain.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    void save(Post post);

    Post findById(Long id);

    void deleteById(Long id);

    List<Post> findByTitle(String title);

    List<Post> findByMemberName(String memberName);

    void increaseReadCount(Long id);

    List<Post> findRecentPostsByCategory(Long categoryId, int limit);

    List<Attachment> findRecentGalleryImages(int limit);

    List<Post> findByBoardId(Long id);
    
    Page<Post> findByBoardIdPaged(Long boardId, Pageable pageable);
}
