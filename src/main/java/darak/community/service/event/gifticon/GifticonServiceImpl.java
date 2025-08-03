package darak.community.service.event.gifticon;

import darak.community.core.auth.ServiceAuth;
import darak.community.domain.gifticon.ClaimStatus;
import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.gifticon.GifticonStatus;
import darak.community.domain.member.Member;
import darak.community.domain.member.MemberGrade;
import darak.community.infra.adaptor.MemberRepositoryAdaptor;
import darak.community.infra.repository.GifticonClaimJpaRepository;
import darak.community.infra.repository.GifticonJpaRepository;
import darak.community.service.event.gifticon.request.GifticonCreateServiceRequest;
import darak.community.service.event.gifticon.response.GifticonClaimResponse;
import darak.community.service.event.gifticon.response.GifticonResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GifticonServiceImpl implements GifticonService {

    private final GifticonJpaRepository gifticonRepository;
    private final GifticonClaimJpaRepository gifticonClaimRepository;
    private final MemberRepositoryAdaptor memberRepository;

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public Long createGifticon(GifticonCreateServiceRequest request) {
        if (request.getTotalQuantity() == null || request.getTotalQuantity() <= 0) {
            log.error("totalQuantity 검증 실패: {}", request.getTotalQuantity());
            throw new IllegalArgumentException("총 수량은 1 이상이어야 합니다. 현재 값: " + request.getTotalQuantity());
        }

        log.info("검증 통과, Gifticon 객체 생성 시작...");

        Gifticon gifticon = request.toEntity();

        log.info("Gifticon 객체 생성 완료");
        log.info("  - totalQuantity: {}", gifticon.getTotalQuantity());
        log.info("  - remainingQuantity: {}", gifticon.getRemainingQuantity());
        log.info("  - status: {}", gifticon.getStatus());

        Gifticon savedGifticon = gifticonRepository.save(gifticon);

        return savedGifticon.getId();
    }

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void activateGifticon(Long gifticonId) {
        Gifticon gifticon = findGiticonBy(gifticonId);
        gifticon.activate();
        log.info("기프티콘 활성화됨: {}", gifticon.getTitle());
    }

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    public void deactivateGifticon(Long gifticonId) {
        Gifticon gifticon = findGiticonBy(gifticonId);
        gifticon.close();
        log.info("기프티콘 비활성화됨: {}", gifticon.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public List<GifticonResponse> findActiveGifticonsAll() {
        return gifticonRepository.findActiveGifticons(GifticonStatus.ACTIVE, LocalDateTime.now())
                .stream()
                .map(GifticonResponse::of)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GifticonResponse> findActiveGifticonsForMember(Long memberId) {
        Member member = findMemberBy(memberId);
        List<Gifticon> activeGifticons = gifticonRepository.findActiveGifticons(GifticonStatus.ACTIVE,
                LocalDateTime.now());

        List<GifticonClaim> memberClaims = gifticonClaimRepository.findByMemberOrderByCreatedDateDesc(member);

        return activeGifticons.stream()
                .map(gifticon -> {
                    GifticonClaim userClaim = memberClaims.stream()
                            .filter(claim -> claim.getGifticon().getId().equals(gifticon.getId()))
                            .findFirst()
                            .orElse(null);

                    boolean isClaimedByUser = userClaim != null;
                    ClaimStatus userClaimStatus = isClaimedByUser ? userClaim.getStatus() : null;

                    return GifticonResponse.of(gifticon, isClaimedByUser, userClaimStatus);
                })
                .toList();
    }

    @Override
    public List<GifticonResponse> findActiveGifticonsLimit(int limit) {
        return gifticonRepository.findActiveGifticons(GifticonStatus.ACTIVE, LocalDateTime.now())
                .stream()
                .limit(limit)
                .map(GifticonResponse::of)
                .toList();
    }

    @Override
    @ServiceAuth(MemberGrade.ADMIN)
    @Transactional(readOnly = true)
    public Page<GifticonResponse> getAllGifticons(Pageable pageable) {
        return gifticonRepository.findAllByOrderByCreatedDateDesc(pageable)
                .map(GifticonResponse::of);
    }

    @Override
    @Transactional(readOnly = true)
    public GifticonResponse getGifticon(Long gifticonId) {
        Gifticon gifticon = findGiticonBy(gifticonId);
        return GifticonResponse.of(gifticon);
    }

    @Override
    @Transactional
    public GifticonClaimResponse claimGifticon(Long gifticonId, Long memberId) {
        Member member = findMemberBy(memberId);
        Gifticon gifticon = findGiticonBy(gifticonId);

        if (gifticonClaimRepository.existsByGifticonAndMember(gifticon, member)) {
            throw new IllegalStateException("이미 수령한 기프티콘입니다.");
        }

        gifticon.claimOne();

        String gifticonCode = generateGifticonCode();

        GifticonClaim claim = GifticonClaim.builder()
                .gifticon(gifticon)
                .member(member)
                .gifticonCode(gifticonCode)
                .build();

        GifticonClaim savedClaim = gifticonClaimRepository.save(claim);
        log.info("기프티콘 수령됨: {} by {}", gifticon.getTitle(), member.getName());
        return GifticonClaimResponse.of(savedClaim);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GifticonClaimResponse> getMemberClaims(Long memberId) {
        Member member = findMemberBy(memberId);
        return gifticonClaimRepository.findByMemberOrderByCreatedDateDesc(member)
                .stream()
                .map(GifticonClaimResponse::of)
                .toList();
    }

    @Override
    public void useGifticon(String gifticonCode, Long memberId) {
        Member member = findMemberBy(memberId);
        GifticonClaim claim = gifticonClaimRepository.findByGifticonCodeAndMember(gifticonCode, member)
                .orElseThrow(() -> new IllegalArgumentException("기프티콘을 찾을 수 없습니다."));

        claim.use();
        log.info("기프티콘 사용됨: {} by {}", claim.getGifticon().getTitle(), member.getName());
    }

    private String generateGifticonCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private Member findMemberBy(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
    }

    private Gifticon findGiticonBy(Long gifticonId) {
        return gifticonRepository.findById(gifticonId)
                .orElseThrow(() -> new IllegalArgumentException("기프티콘을 찾을 수 없습니다."));
    }
}