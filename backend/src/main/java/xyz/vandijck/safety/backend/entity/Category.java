package xyz.vandijck.safety.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "category")
@Indexed
@Data
public class Category implements UniqueEntity, Archivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "category_id", analyze = Analyze.NO, store = Store.NO, index = Index.YES)
    @SortableField(forField = "category_id")
    private long id;

    @Column(name = "name", nullable = false)
    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Analyzer(definition = "namelike")
    private String name;

    @Column(name = "description", length = 1023)
    private String description;

    @IndexedEmbedded
    @JsonBackReference("severityCategoryReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "severity_id", referencedColumnName = "id")
    private Severity severity;

    @IndexedEmbedded
    @JsonBackReference("consequenceCategoryReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "consequence_id", referencedColumnName = "id")
    private Consequence consequence;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "archived", nullable = false)
    private boolean archived;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id == category.id &&
                archived == category.archived &&
                name.equals(category.name) &&
                severity.equals(category.severity) &&
                consequence.equals(category.consequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, severity, consequence, archived);
    }
}
