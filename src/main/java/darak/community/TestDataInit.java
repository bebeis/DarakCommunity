package darak.community;

import darak.community.domain.member.Member;
import darak.community.infra.adaptor.MemberRepositoryAdaptor;
import darak.community.service.member.MemberService;
import darak.community.service.member.request.MemberJoinServiceRequest;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "test-data.init.enabled", havingValue = "true", matchIfMissing = false)
public class TestDataInit implements ApplicationRunner {

    private final MemberService memberService;
    private final MemberRepositoryAdaptor memberRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        joinAdmin();
        joinMember();
    }

    @Transactional
    public void joinAdmin() {
        Member entity = MemberJoinServiceRequest.builder()
                .loginId("admin123")
                .birth(LocalDate.of(1990, 1, 1))
                .name("관리자")
                .email("test@test.com")
                .password("admin123")
                .phone("01012345678")
                .build().toEntity();
        memberRepository.save(entity);
    }

    @Transactional
    public void joinMember() {
        MemberJoinServiceRequest request = MemberJoinServiceRequest.builder()
                .loginId("member123")
                .birth(LocalDate.of(1995, 5, 15))
                .name("일반회원")
                .email("member@member.com")
                .password("member123")
                .phone("01012345677")
                .build();

        memberService.join(request);
        log.info("일반회원 계정 생성 완료: {}", request.getLoginId());
    }
} 