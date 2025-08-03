package darak.community.infra.adaptor;

import darak.community.domain.gifticon.Gifticon;
import darak.community.domain.gifticon.GifticonClaim;
import darak.community.domain.gifticon.GifticonClaimRepository;
import darak.community.domain.member.Member;
import darak.community.infra.repository.GifticonClaimJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GifticonClaimRepositoryAdaptor implements GifticonClaimRepository {

    private final GifticonClaimJpaRepository repository;

    @Override
    public boolean existsByGifticonAndMember(Gifticon gifticon, Member member) {
        return repository.existsByGifticonAndMember(gifticon, member);
    }

    @Override
    public List<GifticonClaim> findByMemberOrderByCreatedDateDesc(Member member) {
        return repository.findByMemberOrderByCreatedDateDesc(member);
    }

    @Override
    public Optional<GifticonClaim> findByGifticonCodeAndMember(String gifticonCode, Member member) {
        return repository.findByGifticonCodeAndMember(gifticonCode, member);
    }
}
