package xyz.vandijck.safety.entity.analysis;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.vandijck.safety.entity.category.Category;


@Data
@AllArgsConstructor
public class CategoryAnalysis {

    private Category category;

    private double priority;

}
