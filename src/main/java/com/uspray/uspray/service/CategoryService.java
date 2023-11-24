package com.uspray.uspray.service;

import com.uspray.uspray.DTO.category.CategoryRequestDto;
import com.uspray.uspray.DTO.category.CategoryResponseDto;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.infrastructure.CategoryRepository;
import com.uspray.uspray.infrastructure.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
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
        categoryRepository.checkDuplicateByNameAndMember(categoryRequestDto.getName(), member);
        int categoryCount = categoryRepository.countCategoryByMember(member);
        Category category = categoryRequestDto.toEntity(member, categoryCount);
        categoryRepository.save(category);
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto deleteCategory(String username, Long categoryId) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        categoryRepository.delete(category);
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto updateCategory(String username, Long categoryId,
        CategoryRequestDto categoryRequestDto) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        category.update(categoryRequestDto);
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto getCategory(String username, Long categoryId) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        return CategoryResponseDto.of(category);
    }

    public CategoryResponseDto updateCategoryOrder(String username, Long categoryId, int order) {
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId,
            memberRepository.getMemberByUserId(username));
        category.updateOrder(order);
        return CategoryResponseDto.of(category);
    }

    public List<CategoryResponseDto> getCategoryList(String username) {
        Member member = memberRepository.getMemberByUserId(username);
        List<Category> categories = categoryRepository.getCategoriesByMemberOrderByOrder(member);
        return categories.stream()
            .map(category -> {
                CategoryResponseDto dto = new CategoryResponseDto();
                dto.setId(category.getId());
                dto.setName(category.getName());
                dto.setColor(category.getColor());
                dto.setOrder(category.getOrder());
                return dto;
            })
            .collect(Collectors.toList());
    }
}
