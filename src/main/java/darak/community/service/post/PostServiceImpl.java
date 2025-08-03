package darak.community.service.post;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.board.Board;
import darak.community.domain.heart.PostHeart;
import darak.community.domain.log.AdminLog;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.post.Attachment;
import darak.community.domain.post.Post;
import darak.community.infra.adaptor.AdminLogRepositoryAdaptor;
import darak.community.infra.adaptor.BoardRepositoryAdaptor;
import darak.community.infra.adaptor.MemberRepositoryAdaptor;
import darak.community.infra.adaptor.PostHeartRepositoryAdaptor;
import darak.community.infra.adaptor.PostRepositoryAdaptor;
import darak.community.infra.adaptor.dto.PostContentDto;
import darak.community.infra.adaptor.dto.PostWithAllDto;
import darak.community.service.post.request.PostCreateServiceRequest;
import darak.community.service.post.request.PostDeleteServiceRequest;
import darak.community.service.post.request.PostSearch;
import darak.community.service.post.request.PostUpdateServiceRequest;
import darak.community.service.post.response.GalleryImageResponse;
import darak.community.service.post.response.PostResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepositoryAdaptor postRepository;
    private final BoardRepositoryAdaptor boardRepository;
    private final MemberRepositoryAdaptor memberRepository;
    private final AdminLogRepositoryAdaptor adminLogRepository;
    private final PostHeartRepositoryAdaptor postHeartRepository;

    @Override
    @Transactional
    public Long createPost(PostCreateServiceRequest request) {
        Member member = findMemberBy(request.getAuthorId());
        Board board = findBoardBy(request.getBoardId());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .anonymous(request.getAnonymous())
                .postType(request.getPostType())
                .member(member)
                .board(board)
                .build();

        postRepository.save(post);
        return post.getId();
    }

    @Override
    @Transactional
    public PostContentDto readPostBy(Long postId, Long memberId) {
        increaseReadCount(postId);
        return postRepository.findPostContentByMemberIdAndPostId(postId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    @Override
    public PostResponse findPostForEditBy(Long postId, Long memberId) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(memberId);

        if (member.getMemberGrade() == MemberGrade.ADMIN || isMemberAuthor(member, post)) {
            return PostResponse.of(post);
        }
        throw new IllegalArgumentException("권한이 없습니다.");
    }

    @Override
    @Transactional
    public void editPost(PostUpdateServiceRequest request) {
        Post post = findPostBy(request.getPostId());
        Member member = findMemberBy(request.getAuthorId());

        validateAuthor(member, post);

        post.edit(request.getTitle(), request.getContent(), request.getPostType(), request.getAnonymous());
    }

    @Override
    @Transactional
    public void deletePostBy(Long postId, Long memberId) {
        Post post = findPostBy(postId);
        Member member = findMemberBy(memberId);

        validateAuthor(member, post);

        // TODO: 단방향 POST - HEART 매핑 vs 양방향 매핑 비교정리
        List<PostHeart> hearts = postHeartRepository.findByPostId(postId);
        hearts.forEach(postHeartRepository::delete);

        postRepository.delete(post);
    }

    @Override
    @Transactional
    @ServiceAuth(MemberGrade.ADMIN)
    public void deletePostByAdmin(PostDeleteServiceRequest request) {
        Post post = findPostBy(request.getPostId());
        Member admin = findMemberBy(request.getMemberId());

        adminLogRepository.save(AdminLog.postDeleteLog(post, admin, request.getReason()));
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void increaseReadCount(Long id) {
        postRepository.findById(id).ifPresent(Post::increaseReadCount);
    }

    @Override
    public Page<PostResponse> findPostsPagedIn(Long boardId, Pageable pageable) {
        List<PostResponse> result = postRepository.findByBoardIdPaged(boardId, pageable)
                .stream()
                .map(PostResponse::of)
                .toList();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public Page<PostResponse> findHeartedPostsBy(Long memberId, Pageable pageable) {
        Page<PostHeart> postHearts = postHeartRepository.findByMemberIdFetchPost(memberId, pageable);
        return postHearts.map(postHeart -> PostResponse.of(postHeart.getPost()));
    }

    @Override
    public long getTotalPostCount() {
        return postRepository.count();
    }

    @Override
    public Page<PostContentDto> findPostsByBoardId(Long boardId, Pageable pageable) {
        return postRepository.findPostsByBoardId(boardId, pageable);
    }

    @Override
    public Page<PostWithAllDto> searchPostsByMemberIdAnd(Long memberId, PostSearch postSearch) {
        Member member = findMemberBy(memberId);
        Pageable pageable = PageRequest.of(postSearch.getPage(), postSearch.getSize());
        Page<PostWithAllDto> posts = postRepository.findPostsWithMetaWrittenByMemberId(memberId, pageable);
        // 여기서 검색 수행
        if (postSearch.getKeyword() == null || postSearch.getKeyword().isEmpty()) {
            return posts;
        }
        List<PostWithAllDto> result = posts.stream()
                .filter(post -> post.getTitle().contains(postSearch.getKeyword())
                        || post.getBoardName().contains(postSearch.getBoardName()))
                .toList();

        return new PageImpl<>(result, pageable, result.size());
    }

    @Override
    public Page<PostWithAllDto> findPostsByMemberIdAndLiked(Long memberId, Pageable pageable) {
        return postRepository.findPostsWithMetaByMemberLiked(memberId, pageable);
    }

    @Override
    public List<GalleryImageResponse> findRecentGalleryImages(int limit) {
        List<GalleryImageResponse> galleryImages = new ArrayList<>();

        // 첨부파일
        List<Attachment> attachmentImages = postRepository.findRecentGalleryImages(limit);
        List<GalleryImageResponse> fromAttachments = attachmentImages.stream()
                .map(GalleryImageResponse::fromAttachment)
                .toList();
        galleryImages.addAll(fromAttachments);

        // content 내부 이미지 URL 추출
        List<Post> galleryPosts = postRepository.findRecentGalleryPostsWithImages(limit * 2);
        for (Post post : galleryPosts) {
            List<String> contentImages = extractImagesFromContent(post.getContent());
            for (String imageUrl : contentImages) {
                // 중복 방지
                boolean alreadyAdded = galleryImages.stream()
                        .anyMatch(img -> img.getPost().getId().equals(post.getId()) &&
                                img.getUrl().equals(imageUrl));

                if (!alreadyAdded) {
                    galleryImages.add(GalleryImageResponse.fromContentImage(imageUrl, post));
                }
            }

            if (galleryImages.size() >= limit) {
                break;
            }
        }

        return galleryImages.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private Board findBoardBy(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시판입니다."));
    }

    private Post findPostBy(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    private void validateAuthor(Member member, Post post) {
        if (!isMemberAuthor(member, post)) {
            throw new IllegalAccessError("권한이 없습니다.");
        }
    }

    private boolean isMemberAuthor(Member member, Post post) {
        return post.getMember().equals(member);
    }


    private List<String> extractImagesFromContent(String content) {
        List<String> imageUrls = new ArrayList<>();

        if (content == null || content.trim().isEmpty()) {
            return imageUrls;
        }

        // Markdown 이미지 패턴: ![alt](url) 또는 ![alt](url "title")
        Pattern markdownPattern = Pattern.compile("!\\[.*?\\]\\(([^\\s)]+)(?:\\s+\".*?\")?\\)");
        Matcher markdownMatcher = markdownPattern.matcher(content);
        while (markdownMatcher.find()) {
            String url = markdownMatcher.group(1);
            if (isImageUrl(url)) {
                imageUrls.add(url);
            }
        }

        // HTML img 태그 패턴: <img src="url" ...>
        Pattern htmlPattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"'][^>]*>");
        Matcher htmlMatcher = htmlPattern.matcher(content);
        while (htmlMatcher.find()) {
            String url = htmlMatcher.group(1);
            if (isImageUrl(url)) {
                imageUrls.add(url);
            }
        }

        return imageUrls;
    }

    private boolean isImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        String lowerUrl = url.toLowerCase();
        return lowerUrl.matches(".*\\.(jpg|jpeg|png|gif|bmp|webp|svg)(\\?.*)?$") ||
                lowerUrl.contains("/uploads/images/") ||
                lowerUrl.contains("picsum.photos");
    }
}
