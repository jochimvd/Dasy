package xyz.vandijck.safety.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.vandijck.safety.entity.category.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
