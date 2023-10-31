package com.uspray.uspray.infrastructure;

import com.uspray.uspray.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

  Category getCategoryById(Long categoryId);
}
