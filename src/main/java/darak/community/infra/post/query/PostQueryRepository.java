package darak.community.infra.post.query;

import darak.community.infra.post.query.dto.PostContentDto;
import darak.community.infra.post.query.dto.PostWithAllDto;
import darak.community.service.post.request.PostSearch;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostQueryRepository {

    Page<PostWithAllDto> findPostsWithMetaByBoardId(Long boardId, Pageable pageable);

    Page<PostWithAllDto> findPostsWithMetaWrittenByMemberId(Long memberId, Pageable pageable);

    Page<PostWithAllDto> findPostsWithMetaByMemberLiked(Long memberId, Pageable pageable);

    Optional<PostContentDto> findPostContentByMemberIdAndPostId(Long postId, Long memberId);

    Page<PostContentDto> findPostsByBoardId(Long boardId, Pageable pageable);

    Page<PostWithAllDto> searchPostsWithMetaByMemberIdAnd(Long memberId, PostSearch postSearch, Pageable pageable);

}
