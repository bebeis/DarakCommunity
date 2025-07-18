package darak.community.web.controller.community.comment;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.service.comment.CommentService;
import darak.community.service.comment.request.CommentCreateServiceRequest;
import darak.community.service.comment.request.ReplyCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/community/boards/{boardId}/posts/{postId}/comments")
    public String createComment(@Login LoginMember loginMember, @PathVariable Long boardId, @PathVariable Long postId,
                                @ModelAttribute @Validated CommentCreateForm form, BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/community/boards/" + boardId + "/posts/" + postId;
        }

        commentService.createFromPost(form.toServiceRequest(loginMember.getId(), postId));
        return "redirect:/community/boards/" + boardId + "/posts/" + postId;
    }

    @PostMapping("/community/boards/{boardId}/posts/{postId}/comments/{parentCommentId}/reply")
    public String createReply(@Login LoginMember loginMember, @PathVariable Long boardId, @PathVariable Long postId,
                              @PathVariable Long parentCommentId,
                              @ModelAttribute @Validated CommentCreateForm form, BindingResult bindingResult,
                              @RequestParam(defaultValue = "0") int commentPage,
                              Model model) {
        if (bindingResult.hasErrors()) {
            return "redirect:/community/boards/" + boardId + "/posts/" + postId + "?commentPage=" + commentPage;
        }

        commentService.createReplyFromPost(form.toReplyServiceRequest(loginMember.getId(), postId, parentCommentId));
        return "redirect:/community/boards/" + boardId + "/posts/" + postId + "?commentPage=" + commentPage;
    }

    @DeleteMapping("/community/comments/{commentId}")
    public String deleteComment(@Login LoginMember loginMember, @PathVariable Long commentId,
                                @RequestParam(required = false) Long boardId,
                                @RequestParam(required = false) Long postId,
                                @RequestParam(defaultValue = "0") int commentPage,
                                RedirectAttributes redirectAttributes) {
        try {
            commentService.deleteCommentBy(commentId, loginMember.getId());
            redirectAttributes.addFlashAttribute("message", "댓글이 성공적으로 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (boardId != null && postId != null) {
            return "redirect:/community/boards/" + boardId + "/posts/" + postId + "?commentPage=" + commentPage;
        }

        return "redirect:/community";
    }

    @Data
    @AllArgsConstructor
    static class CommentCreateForm {

        @NotEmpty(message = "댓글 내용을 입력해주세요.")
        private String content;
        private boolean anonymous;

        public CommentCreateForm() {
            this.anonymous = false; // 기본값 설정
        }

        public CommentCreateServiceRequest toServiceRequest(Long memberId, Long postId) {
            return CommentCreateServiceRequest.builder()
                    .content(content)
                    .isAnonymous(anonymous)
                    .memberId(memberId)
                    .postId(postId)
                    .build();
        }

        public ReplyCreateServiceRequest toReplyServiceRequest(Long memberId, Long postId, Long parentCommentId) {
            return ReplyCreateServiceRequest.builder()
                    .content(content)
                    .isAnonymous(anonymous)
                    .memberId(memberId)
                    .postId(postId)
                    .parentCommentId(parentCommentId)
                    .build();
        }
    }
}
