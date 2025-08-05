package darak.community.core.session;

import darak.community.core.session.constant.SessionConst;
import darak.community.core.session.dto.LoginMember;
import darak.community.domain.member.Member;
import darak.community.infra.member.MemberRepositoryAdaptor;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionManager {

    private final MemberRepositoryAdaptor memberRepositoryAdaptor;

    public void logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

    public void login(HttpSession session, @NotEmpty String loginId) {
        Member member = memberRepositoryAdaptor.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        registerMemberIn(session, LoginMember.of(member.getId(), member.getMemberGrade()));
    }

    private void registerMemberIn(HttpSession session, LoginMember loginMember) {
        session.setAttribute(SessionConst.LOGIN_MEMBER, loginMember);
    }
}
