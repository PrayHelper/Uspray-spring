package com.uspray.uspray.service;

import com.uspray.uspray.DTO.pray.PrayListResponseDto;
import com.uspray.uspray.DTO.pray.request.PrayRequestDto;
import com.uspray.uspray.DTO.pray.request.PrayResponseDto;
import com.uspray.uspray.Enums.PrayType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.domain.Pray;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import com.uspray.uspray.infrastructure.PrayRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrayService {

  private final PrayRepository prayRepository;
  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;

  @Transactional
  public PrayResponseDto createPray(PrayRequestDto prayRequestDto, String username) {
    Member member = memberRepository.getMemberByUserId(username);
    Category category = categoryRepository.getCategoryById(prayRequestDto.getCategoryId());
    if (!Objects.equals(category.getMember().getId(), member.getId())) {
      throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    Pray pray = prayRequestDto.toEntity(member, category, PrayType.PERSONAL);
    prayRepository.save(pray);
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public PrayResponseDto getPrayDetail(Long prayId, String username) {
    Pray pray = prayRepository.getPrayById(prayId);
    if (!pray.getMember().getId().equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public PrayResponseDto deletePray(Long prayId, String username) {
    Pray pray = prayRepository.findById(prayId)
        .orElseThrow(() -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
            ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    if (!pray.getMember().getId().equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    prayRepository.delete(pray);
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public PrayResponseDto updatePray(Long prayId, String username, PrayRequestDto prayRequestDto) {
    Pray pray = prayRepository.findById(prayId)
        .orElseThrow(() -> new NotFoundException(ErrorStatus.PRAY_NOT_FOUND_EXCEPTION,
            ErrorStatus.PRAY_NOT_FOUND_EXCEPTION.getMessage()));
    if (!pray.getMember().getId().equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.PRAY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    Category category = categoryRepository.getCategoryById(prayRequestDto.getCategoryId());
    if (!Objects.equals(category.getMember().getId(), pray.getMember().getId())) {
      throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    pray.update(prayRequestDto);
    return PrayResponseDto.of(pray);
  }

  @Transactional
  public List<PrayListResponseDto> getPrayList(String username, String orderType) {
    List<Pray> prays = prayRepository.findAllWithOrder(orderType, username);

    // Pray 엔티티를 categoryId를 기준으로 그룹화한 맵 생성
    Map<Long, List<Pray>> prayMap = prays.stream()
        .collect(Collectors.groupingBy(pray -> pray.getCategory().getId()));

    // 그룹화된 맵을 PrayListResponseDto 변환하여 반환
    return prayMap.entrySet().stream()
        .map(entry -> new PrayListResponseDto(entry.getKey(),
            entry.getValue().stream()
                .map(PrayResponseDto::of)
                .collect(Collectors.toList())))
        .collect(Collectors.toList());
  }
}
