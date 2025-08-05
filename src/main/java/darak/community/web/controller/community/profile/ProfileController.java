package darak.community.web.controller.community.profile;

import darak.community.core.argumentresolver.Login;
import darak.community.core.session.dto.LoginMember;
import darak.community.infra.comment.query.CommentQueryRepository;
import darak.community.infra.comment.query.dto.CommentWithMetaDto;
import darak.community.infra.post.query.PostQueryRepository;
import darak.community.infra.post.query.dto.PostWithAllDto;
import darak.community.service.comment.CommentService;
import darak.community.service.comment.request.CommentSearch;
import darak.community.service.member.MemberService;
import darak.community.service.member.ProfileService;
import darak.community.service.member.request.ProfileUpdateServiceRequest;
import darak.community.service.member.response.ProfileResponse;
import darak.community.service.member.response.ProfileStatsResponse;
import darak.community.service.post.PostService;
import darak.community.service.post.request.PostSearch;
import darak.community.web.dto.PasswordForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final PostQueryRepository postQueryRepository;
    private final CommentQueryRepository commentQueryRepository;

    @GetMapping
    public String profile(@Login LoginMember loginMember, Model model) {

        ProfileResponse profileResponse = profileService.getProfileBy(loginMember.getId());
        ProfileStatsResponse profileStats = profileService.getProfileStats(loginMember.getId());

        model.addAttribute("profile", profileResponse);
        model.addAttribute("active", "profile");
        model.addAttribute("stats", profileStats);

        return "community/profile/memberProfile";
    }

    @GetMapping("/{memberId}")
    public String memberProfile(@PathVariable Long memberId, Model model) {

        ProfileResponse profileResponse = profileService.getProfileBy(memberId);
        ProfileStatsResponse profileStats = profileService.getProfileStats(memberId);

        model.addAttribute("profile", profileResponse);
        model.addAttribute("active", "profile");
        model.addAttribute("stats", profileStats);

        return "community/profile/memberProfile";
    }

    @GetMapping("/posts")
    public String myPosts(@Login LoginMember loginMember,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(defaultValue = "10") int size,
                          @RequestParam(required = false) String keyword,
                          @RequestParam(required = false) String boardName,
                          Model model) {

        Page<PostWithAllDto> postWithMetaDtos = postQueryRepository.searchPostsWithMetaByMemberIdAnd(
                loginMember.getId(),
                PostSearch.builder()
                        .keyword(keyword)
                        .boardName(boardName)
                        .page(page)
                        .size(size)
                        .build(), PageRequest.of(page, size));

        model.addAttribute("posts", postWithMetaDtos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardName", boardName);
        model.addAttribute("active", "posts");

        return "community/profile/myPosts";
    }

    @GetMapping("/comments")
    public String myComments(@Login LoginMember loginMember,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             @RequestParam(required = false) String keyword,
                             @RequestParam(required = false) String boardName,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CommentWithMetaDto> commentWithMetaDtos = commentQueryRepository.searchCommentsWithMetaByMemberIdAnd(
                loginMember.getId(),
                CommentSearch.builder()
                        .keyword(keyword)
                        .boardName(boardName)
                        .page(page)
                        .size(size)
                        .build(), PageRequest.of(page, size));
        
        model.addAttribute("comments", commentWithMetaDtos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("boardName", boardName);
        model.addAttribute("active", "comments");

        return "community/profile/myComments";
    }

    @GetMapping("/liked-posts")
    public String likedPosts(@Login LoginMember loginMember,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PostWithAllDto> postWithMetaDtos = postQueryRepository.findPostsWithMetaByMemberLiked(loginMember.getId(),
                pageable);

        model.addAttribute("posts", postWithMetaDtos);
        model.addAttribute("active", "liked-posts");

        return "community/profile/likedPosts";
    }

    @GetMapping("/liked-comments")
    public String likedComments(@Login LoginMember loginMember,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CommentWithMetaDto> commentWithMetaDtos = commentQueryRepository.findCommentsWithMetaByMemberLiked(
                loginMember.getId(), pageable);

        model.addAttribute("comments", commentWithMetaDtos);
        model.addAttribute("active", "liked-comments");

        return "community/profile/likedComments";
    }

    @GetMapping("/edit")
    public String editProfile(@Login LoginMember loginMember, Model model) {

        ProfileResponse profileResponse = profileService.getProfileBy(loginMember.getId());
        model.addAttribute("member", profileResponse);
        model.addAttribute("active", "edit");

        return "community/profile/editProfile";
    }

    @PostMapping("/edit")
    public String updateProfile(@Login LoginMember loginMember,
                                @ModelAttribute ProfileUpdateForm form, BindingResult bindingResult,
                                HttpSession session, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "입력값에 오류가 있습니다.");
        }
        profileService.updateProfile(loginMember.getId(), form.toServiceRequest());
        redirectAttributes.addFlashAttribute("message", "프로필이 성공적으로 수정되었습니다.");

        return "redirect:/profile/edit";
    }

    @PostMapping("/change-password")
    public String changePassword(@Login LoginMember loginMember, @ModelAttribute PasswordForm form,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "입력값에 오류가 있습니다.");
        }
        if (!form.isNewPasswordMatch()) {
            redirectAttributes.addFlashAttribute("error", "새 비밀번호가 일치하지 않습니다.");
        }

        try {
            memberService.changePassword(form.toServiceRequest(loginMember.getId()));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "비밀번호 변경에 실패했습니다. 이유: " + e.getMessage());
        }

        return "redirect:/profile/edit";
    }


    @Data
    private class ProfileUpdateForm {

        @NotEmpty
        private String email;

        private String phone;

        public ProfileUpdateServiceRequest toServiceRequest() {
            return ProfileUpdateServiceRequest.builder()
                    .email(email)
                    .phone(phone)
                    .build();
        }

    }
}