package darak.community.domain.gifticon;

import darak.community.domain.member.Member;
import java.util.List;
import java.util.Optional;

public interface GifticonClaimRepository {

    boolean existsByGifticonAndMember(Gifticon gifticon, Member member);

    List<GifticonClaim> findByMemberOrderByCreatedDateDesc(Member member);

    Optional<GifticonClaim> findByGifticonCodeAndMember(String gifticonCode, Member member);

}
