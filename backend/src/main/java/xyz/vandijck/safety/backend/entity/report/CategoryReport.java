package xyz.vandijck.safety.backend.entity.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import xyz.vandijck.safety.backend.entity.Category;


@Data
@AllArgsConstructor
public class CategoryReport {

    private Category category;

    private double score;

}
