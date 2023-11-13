package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Category;
import com.uspray.uspray.domain.Member;
import com.uspray.uspray.exception.ErrorStatus;
import com.uspray.uspray.exception.model.NotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Category getCategoryById(Long categoryId);
    
    default Category getCategoryByIdAndMember(Long categoryId, Member member) {
        return findById(categoryId)
            .filter(category -> category.getMember().equals(member))
            .orElseThrow(() -> new NotFoundException(
                ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION,
                ErrorStatus.CATEGORY_UNAUTHORIZED_EXCEPTION.getMessage()
            ));
    }
    
    boolean existsCategoryByNameAndMember(String name, Member member);
    
    int countCategoryByMember(Member member);
    
    boolean existsCategoryByIdAndMember(Long categoryId, Member member);
}
