package darak.community.service.member;

import darak.community.core.auth.ServiceAuth;
import darak.community.core.context.UserContext;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.domain.member.MemberRepository;
import darak.community.service.member.request.MemberJoinServiceRequest;
import darak.community.service.member.request.PasswordChangeServiceRequest;
import darak.community.service.member.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public MemberResponse join(MemberJoinServiceRequest request) {
        validateDuplicateMember(request.getLoginId());
        Member joinMember = memberRepository.save(request.toEntity());
        return MemberResponse.of(joinMember);
    }

    // AOP 적용 시 주의: 내부 메서드 호출
    @Override
    public void validateDuplicateMember(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 존재하는 회원입니다.");
        }
    }

    @Transactional
    @Override
    public void changePassword(PasswordChangeServiceRequest request) {
        Member findMember = findMemberBy(request.getMemberId());
        findMember.changePassword(request.getNewPassword(), request.getOldPassword());
    }

    @Transactional
    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void editMemberGrade(Long targetMemberId, MemberGrade grade) {
        Member target = findMemberBy(targetMemberId);
        target.changeMemberGrade(grade);
        log.info("{}가 회원 등급 변경: 대상 회원 ID = {}, 새로운 등급 = {}",
                UserContext.getCurrentMemberId(), targetMemberId, grade);
    }

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public long getTotalMemberCount() {
        return memberRepository.count();
    }

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public Page<MemberResponse> getAllMembersPaged(Pageable pageable) {
        Page<Member> members = memberRepository.findAllPaged(pageable);
        return members.map(MemberResponse::of);
    }

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public Page<MemberResponse> searchMembers(String keyword, MemberGrade grade, Pageable pageable) {
        Page<Member> members = memberRepository.searchMembers(keyword, grade, pageable);
        return members.map(MemberResponse::of);
    }

    private Member findMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }
}
