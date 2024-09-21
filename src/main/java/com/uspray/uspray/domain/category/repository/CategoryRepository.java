package com.uspray.uspray.domain.category.repository;

import com.uspray.uspray.global.enums.CategoryType;
import com.uspray.uspray.domain.category.model.Category;
import com.uspray.uspray.domain.member.model.Member;
import com.uspray.uspray.global.exception.ErrorStatus;
import com.uspray.uspray.global.exception.model.CustomException;
import com.uspray.uspray.global.exception.model.NotFoundException;
import com.uspray.uspray.domain.category.repository.querydsl.CategoryRepositoryCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>,
    CategoryRepositoryCustom {

    Category getCategoryById(Long categoryId);

    List<Category> getCategoriesByMemberOrderByOrder(Member member);

    List<Category> getCategoriesByMemberAndCategoryTypeOrderByOrder(Member member,
        CategoryType categoryType);

    default Category getCategoryByIdAndMember(Long categoryId, Member member) {
        Category category = findById(categoryId)
            .filter(c ->  c.getMember().equals(member))
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION
            ));

        if (!category.getCategoryType().equals(CategoryType.PERSONAL)) {
            throw new CustomException(ErrorStatus.PRAY_CATEGORY_TYPE_MISMATCH);
        }

        return category;
    }

    default void checkDuplicate(String name, Member member, CategoryType type) {
        if (existsByNameAndMemberAndCategoryType(name, member, type)) {
            throw new NotFoundException(ErrorStatus.CATEGORY_DUPLICATE_EXCEPTION);
        }
    }


    default int checkDuplicateAndReturnMaxOrder(String name, Member member, CategoryType type) {
        checkDuplicate(name, member, type);
        if (getCategoriesByMemberAndCategoryTypeOrderByOrder(member, type).isEmpty()) {
            return 0;
        }
        return getMaxCategoryOrder(member, type);
    }

    boolean existsByNameAndMemberAndCategoryType(String name, Member member, CategoryType type);

    default int getMaxCategoryOrder(Member member, CategoryType type) {
        Category category = getCategoriesByMemberAndCategoryTypeOrderByOrder(member, type).stream()
            .reduce((first, second) -> second)
            .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION));
        return category.getOrder();
    }
}