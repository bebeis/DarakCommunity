package darak.community.service.login;

import darak.community.domain.member.Member;
import darak.community.infra.adaptor.MemberRepositoryAdaptor;
import darak.community.service.login.request.LoginServiceRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final MemberRepositoryAdaptor memberRepository;

    @Override
    @Transactional
    public void authenticate(LoginServiceRequest request) {
        Member member = findMemberBy(request.getLoginId());
        member.validatePassword(request.getRawPassword());
        // TODO: 로그인 기록 로깅 or 저장 로직 추가
    }

    @Override
    public void validateMemberPasswordExpiration(String loginId) {
        Member member = findMemberBy(loginId);
        if (member.isPasswordExpired()) {
            throw new IllegalArgumentException("비밀번호가 만료되었습니다. 비밀번호를 변경해주세요.");
        }
    }

    private Member findMemberBy(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
