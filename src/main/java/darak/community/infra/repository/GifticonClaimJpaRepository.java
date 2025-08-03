package darak.community.infra.repository;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.member.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GifticonClaimJpaRepository extends JpaRepository<GifticonClaim, Long> {

    boolean existsByGifticonAndMember(Gifticon gifticon, Member member);

    List<GifticonClaim> findByMemberOrderByCreatedDateDesc(Member member);

    Optional<GifticonClaim> findByGifticonCodeAndMember(String gifticonCode, Member member);

} 