package darak.community.service.post;

import darak.community.infra.adaptor.dto.PostContentDto;
import darak.community.infra.adaptor.dto.PostWithAllDto;
import darak.community.service.post.request.PostCreateServiceRequest;
import darak.community.service.post.request.PostDeleteServiceRequest;
import darak.community.service.post.request.PostSearch;
import darak.community.service.post.request.PostUpdateServiceRequest;
import darak.community.service.post.response.GalleryImageResponse;
import darak.community.service.post.response.PostResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Long createPost(PostCreateServiceRequest request);

    PostContentDto readPostBy(Long postId, Long memberId);

    PostResponse findPostForEditBy(Long postId, Long memberId);

//    Page<PostWithAllDto> findPostsWithMetaByMemberPaged(Long memberId, Pageable pageable);

    Page<PostResponse> findPostsPagedIn(Long boardId, Pageable pageable);

    Page<PostResponse> findHeartedPostsBy(Long memberId, Pageable pageable);

    void increaseReadCount(Long id);

    void editPost(PostUpdateServiceRequest request);

    void deletePostBy(Long postId, Long memberId);

    void deletePostByAdmin(PostDeleteServiceRequest request);

    long getTotalPostCount();

    Page<PostContentDto> findPostsByBoardId(Long boardId, Pageable pageable);

    Page<PostWithAllDto> searchPostsByMemberIdAnd(Long memberId, PostSearch postSearch);

    Page<PostWithAllDto> findPostsByMemberIdAndLiked(Long memberId, Pageable pageable);

    List<GalleryImageResponse> findRecentGalleryImages(int limit);
}
