package xyz.vandijck.safety.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "company")
@Indexed
@Data
public class Company implements UniqueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "company_id", analyze = Analyze.NO, store = Store.NO, index = Index.YES)
    @SortableField(forField = "company_id")
    private long id;

    @NotBlank
    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Column(name = "name", unique = true, nullable = false)
    @Analyzer(definition = "namelike")
    private String name;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return id == company.id &&
                name.equals(company.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
