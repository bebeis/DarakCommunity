package darak.community.web.controller.community.post;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.infra.comment.query.CommentQueryRepository;
import darak.community.infra.post.query.PostQueryRepository;
import darak.community.service.board.BoardService;
import darak.community.service.board.response.BoardResponse;
import darak.community.service.comment.CommentService;
import darak.community.service.post.PostHeartService;
import darak.community.service.post.PostService;
import darak.community.web.dto.PostCURequestForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final BoardService boardService;
    private final PostService postService;
    private final CommentService commentService;
    private final PostHeartService postHeartService;
    private final PostQueryRepository postQueryRepository;
    private final CommentQueryRepository commentQueryRepository;

    @ModelAttribute
    public void addSideMenuInformation(Model model, @PathVariable Long boardId) {
        BoardResponse currentBoard = boardService.findBoardInfoBy(boardId);
        model.addAttribute("currentBoard", currentBoard);
        // 어차피 CommunityControllerAdvice 에서 전역 ModelAttribute 로 게시판 정보를 추가하므로, 현재 게시판 정보만 알면 됨
    }

    @GetMapping("/community/boards/{boardId}/posts/{postId}")
    public String viewPost(@Login LoginMember loginMember,
                           @PathVariable Long boardId,
                           @PathVariable Long postId,
                           @RequestParam(defaultValue = "0") int commentPage,
                           Model model) {

        model.addAttribute("post", postQueryRepository.findPostContentByMemberIdAndPostId(postId, loginMember.getId()));
        model.addAttribute("commentsPaged",
                commentQueryRepository.findCommentInPostByPostIdAndMemberIdPaged(loginMember.getId(), postId,
                        PageRequest.of(commentPage, 5)));
        model.addAttribute("currentCommentPage", commentPage);
        model.addAttribute("isLiked", postHeartService.isLiked(postId, loginMember.getId()));

        return "community/post/viewPost";
    }

    @GetMapping("/community/boards/{boardId}/posts")
    public String writePostForm(@PathVariable Long boardId,
                                @ModelAttribute PostCURequestForm form, Model model) {

        return "community/post/createPostForm";
    }

    @PostMapping("/community/boards/{boardId}/posts")
    public String writePost(@Login LoginMember loginMember, @PathVariable Long boardId,
                            @ModelAttribute @Validated PostCURequestForm form, BindingResult bindingResult,
                            Model model) {

        if (bindingResult.hasErrors()) {
            return "community/post/createPostForm";
        }

        Long postId = postService.createPost(form.toCreateServiceRequest(loginMember.getId(), boardId));
        return "redirect:/community/boards/" + boardId + "/posts/" + postId;
    }

    @DeleteMapping("/community/boards/{boardId}/posts/{postId}")
    public String deletePost(@Login LoginMember loginMember,
                             @PathVariable Long boardId,
                             @PathVariable Long postId,
                             RedirectAttributes attributes,
                             Model model) {

        postService.deletePostBy(postId, loginMember.getId());
        attributes.addAttribute("deleteStatus", true);
        return "redirect:/community/boards/" + boardId;
    }

    @GetMapping("/community/boards/{boardId}/posts/{postId}/edit")
    public String editPostForm(@Login LoginMember loginMember,
                               @PathVariable Long boardId,
                               @PathVariable Long postId,
                               @ModelAttribute PostCURequestForm form,
                               Model model) {

        model.addAttribute("form", new PostCURequestForm(postService.findPostForEditBy(postId, loginMember.getId())));
        return "community/post/editPostForm";
    }

    @PatchMapping("/community/boards/{boardId}/posts/{postId}/edit")
    public String editPost(@Login LoginMember loginMember, @PathVariable Long boardId, @PathVariable Long postId,
                           @ModelAttribute @Validated PostCURequestForm form, BindingResult bindingResult,
                           RedirectAttributes attributes, Model model) {

        if (bindingResult.hasErrors()) {
            return "community/post/editPostForm";
        }

        postService.editPost(form.toUpdateServiceRequest(postId, loginMember.getId(), boardId));
        attributes.addAttribute("editStatus", true);
        return "redirect:/community/boards/" + boardId + "/posts/" + postId;
    }

}
