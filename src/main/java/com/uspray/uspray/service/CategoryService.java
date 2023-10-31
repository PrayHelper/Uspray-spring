package com.uspray.uspray.service;

import com.uspray.uspray.DTO.category.CategoryRequestDto;
import com.uspray.uspray.DTO.category.CategoryResponseDto;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final MemberRepository memberRepository;
  private final CategoryRepository categoryRepository;

  public CategoryResponseDto createCategory(String username,
      CategoryRequestDto categoryRequestDto) {
    Member member = memberRepository.getMemberByUserId(username);
    Category category = categoryRequestDto.toEntity(member);
    categoryRepository.save(category);
    return CategoryResponseDto.of(category);
  }

  public CategoryResponseDto deleteCategory(String username, Long categoryId) {
    Category category = categoryRepository.getCategoryById(categoryId);
    if (!category.getMember().getId()
        .equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    return CategoryResponseDto.of(category);
  }

  public CategoryResponseDto updateCategory(String username, Long categoryId,
      CategoryRequestDto categoryRequestDto) {
    Category category = categoryRepository.getCategoryById(categoryId);
    if (!category.getMember().getId()
        .equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    category.update(categoryRequestDto);
    return CategoryResponseDto.of(category);
  }

  public CategoryResponseDto getCategory(String username, Long categoryId) {
    Category category = categoryRepository.getCategoryById(categoryId);
    if (!category.getMember().getId()
        .equals(memberRepository.getMemberByUserId(username).getId())) {
      throw new NotFoundException(ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
          ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage());
    }
    return CategoryResponseDto.of(category);
  }
}
