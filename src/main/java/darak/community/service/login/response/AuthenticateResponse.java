package darak.community.service.login.response;

import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthenticateResponse {

    private Long memberId;
    private String loginId;
    private String nickname;
    private MemberGrade memberGrade;
    private boolean isPasswordExpired;

    @Builder
    private AuthenticateResponse(Long memberId, String loginId, String nickname, MemberGrade memberGrade,
                                 boolean isPasswordExpired) {
        this.memberId = memberId;
        this.loginId = loginId;
        this.nickname = nickname;
        this.memberGrade = memberGrade;
        this.isPasswordExpired = isPasswordExpired;
    }

    public static AuthenticateResponse from(Member member) {
        return AuthenticateResponse.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getName())
                .memberGrade(member.getMemberGrade())
                .isPasswordExpired(member.isPasswordExpired())
                .build();
    }

    public static AuthenticateResponse withExpiredPassword(Member member) {
        return AuthenticateResponse.builder()
                .memberId(member.getId())
                .loginId(member.getLoginId())
                .nickname(member.getName())
                .memberGrade(member.getMemberGrade())
                .isPasswordExpired(true)
                .build();
    }
}
