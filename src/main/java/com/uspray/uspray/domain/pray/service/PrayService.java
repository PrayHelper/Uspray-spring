package com.uspray.uspray.domain.pray.service;

import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.domain.pray.dto.pray.response.PrayResponseDto;
import com.uspray.uspray.domain.pray.model.Pray;
import com.uspray.uspray.domain.pray.repository.PrayRepository;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

    private final PrayRepository prayRepository;

    @Transactional
    public PrayResponseDto getPrayDetail(Long prayId, String username) {
        Pray pray = prayRepository.getPrayByIdAndMemberId(prayId, username);
        return PrayResponseDto.of(pray);
    }

    public PrayResponseDto savePray(Pray pray) {
        return PrayResponseDto.of(prayRepository.save(pray));
    }

    public List<Pray> getPrayListByCategory(Category category) {
        return prayRepository.findAllByCategory(category);
    }

    public List<Pray> getPrayListByMemberAndCategory(Member member, Category category) {
        return prayRepository.findAllByMemberAndCategoryOrderByCreatedAtAsc(member, category);
    }

    public Pray getPrayByIdAndMemberId(Long prayId, String username) {
        return prayRepository.getPrayByIdAndMemberId(prayId, username);
    }

    public Pray getSharedPray(Long prayId) {
        return prayRepository.getPrayByOriginPrayId(prayId);
    }

    public List<Pray> getPrayListDeadlineBefore(LocalDate currentDate) {
        return prayRepository.findAllByDeadlineBefore(currentDate);
    }

    public Integer getSharedCountByOriginPrayId(Long originPrayId) {
        return prayRepository.getSharedCountByOriginPrayId(originPrayId);
    }

    public void deletePray(Pray pray) {
        prayRepository.delete(pray);
    }

    public Pray cancelPray(Long prayId, String username) {
        return prayRepository.cancelPray(prayId, username);
    }
}
