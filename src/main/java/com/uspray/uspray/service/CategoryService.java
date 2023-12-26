package com.uspray.uspray.service;

import com.uspray.uspray.DTO.category.CategoryRequestDto;
import com.uspray.uspray.DTO.category.CategoryResponseDto;
import com.uspray.uspray.Enums.CategoryType;
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

        int maxCategoryOrder = categoryRepository.checkDuplicateAndReturnMaxOrder(
            categoryRequestDto.getName(), member);
        Category category = categoryRequestDto.toEntity(member, maxCategoryOrder + 1024);
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

    public CategoryResponseDto updateCategoryOrder(String username, Long categoryId, int index) {
        Member member = memberRepository.getMemberByUserId(username);
        Category category = categoryRepository.getCategoryByIdAndMember(categoryId, member);
        List<Category> categories = categoryRepository.getCategoriesByMemberOrderByOrder(member);

        Category nextCategory = categories.get(index);
        Category prevCategory = (index > 0) ? categories.get(index - 1) : null;

        int newOrder = (index == 0)
            ? nextCategory.getOrder() / 2
            : (index == categories.size() - 1)
                ? prevCategory.getOrder() + 1024
                : (prevCategory.getOrder() + nextCategory.getOrder()) / 2;

        category.updateOrder(newOrder);

        categoryRepository.save(category);

        return CategoryResponseDto.of(category);
    }

    public List<CategoryResponseDto> getCategoryList(String username, CategoryType categoryType) {
        Member member = memberRepository.getMemberByUserId(username);
        List<Category> categories = categoryRepository.getCategoriesByMemberAndCategoryTypeOrderByOrder(
            member, categoryType);
        return categories.stream()
            .map(CategoryResponseDto::of)
            .collect(Collectors.toList());
    }
}
