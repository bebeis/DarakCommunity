package darak.community.infra.comment.query;

import darak.community.infra.comment.query.dto.CommentInPostDto;
import darak.community.infra.comment.query.dto.CommentWithMetaDto;
import darak.community.service.comment.request.CommentSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentQueryRepository {

    Page<CommentWithMetaDto> findCommentsWithMetaByMemberIdPaged(Long memberId, Pageable pageable);

    Page<CommentWithMetaDto> findCommentsWithMetaByMemberLiked(Long memberId, Pageable pageable);

    Page<CommentInPostDto> findCommentInPostByPostIdAndMemberIdPaged(Long postId, Long memberId, Pageable pageable);

    Page<CommentWithMetaDto> searchCommentsWithMetaByMemberIdAnd(Long memberId, CommentSearch commentSearch,
                                                                 Pageable pageable);
}
