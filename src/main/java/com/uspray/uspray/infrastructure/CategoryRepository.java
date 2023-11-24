package com.uspray.uspray.infrastructure;

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

    default Category getCategoryByIdAndMember(Long categoryId, Member member) {
        return findById(categoryId)
            .filter(category -> category.getMember().equals(member))
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION,
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION.getMessage()
            ));
    }


    default boolean checkDuplicateByNameAndMember(String name, Member member) {
        boolean isDuplicate = existsByNameAndMember(name, member);
        if (isDuplicate) {
            throw new NotFoundException(ErrorStatus.CATEGORY_DUPLICATE_EXCEPTION,
                ErrorStatus.CATEGORY_DUPLICATE_EXCEPTION.getMessage());
        }
        return false;
    }

    boolean existsByNameAndMember(String name, Member member);

    default int countCategoryByMember(Member member) {
        List<Category> categories = getCategoriesByMemberOrderByOrder(member);
        int count = categories.size();
        if (count > 7) {
            throw new NotFoundException(ErrorStatus.CATEGORY_LIMIT_EXCEPTION,
                ErrorStatus.CATEGORY_LIMIT_EXCEPTION.getMessage());
        }
        return count;
    }

    default int getMaxCategoryOrder(Member member) {
        Category category = getCategoriesByMemberOrderByOrder(member).stream()
            .reduce((first, second) -> second)
            .orElseThrow(() -> new NotFoundException(ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION,
                ErrorStatus.CATEGORY_NOT_FOUND_EXCEPTION.getMessage()));
        return category.getOrder();
    }
}
