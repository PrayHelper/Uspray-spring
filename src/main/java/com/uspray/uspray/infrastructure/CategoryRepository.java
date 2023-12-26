package com.uspray.uspray.infrastructure;

import com.uspray.uspray.Enums.CategoryType;
import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Category getCategoryById(Long categoryId);

    List<Category> getCategoriesByMemberOrderByOrder(Member member);

    List<Category> getCategoriesByMemberAndCategoryTypeOrderByOrder(Member member,
        CategoryType categoryType);

    default Category getCategoryByIdAndMember(Long categoryId, Member member) {
        return findById(categoryId)
            .filter(category -> category.getMember().equals(member))
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION,
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION.getMessage()
            ));
    }


    default int checkDuplicateAndReturnMaxOrder(String name, Member member, CategoryType type) {
        boolean isDuplicate = existsByNameAndMemberAndCategoryType(name, member, type);
        if (isDuplicate) {
            throw new NotFoundException(ErrorStatus.CATEGORY_DUPLICATE_EXCEPTION,
                ErrorStatus.CATEGORY_DUPLICATE_EXCEPTION.getMessage());
        }
        if (getCategoriesByMemberAndCategoryTypeOrderByOrder(member, type).isEmpty()) {
            return 0;
        }
        return getMaxCategoryOrder(member, type);
    }

    boolean existsByNameAndMemberAndCategoryType(String name, Member member, CategoryType type);

    default int getMaxCategoryOrder(Member member, CategoryType type) {
        Category category = getCategoriesByMemberAndCategoryTypeOrderByOrder(member, type).stream()
            .reduce((first, second) -> second)
            .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION,
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION.getMessage()));
        return category.getOrder();
    }
}
