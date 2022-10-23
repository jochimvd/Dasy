package xyz.vandijck.safety.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "consequence")
@Indexed
@Data
public class Consequence implements UniqueEntity, Archivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "consequence_id", store = Store.YES, analyze = Analyze.NO)
    private long id;

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Column(name = "name", nullable = false)
    @Analyzer(definition = "namelike")
    private String name;

    @Column(name = "description", length = 1023)
    private String description;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "probability", nullable = false)
    private double probability;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "archived", nullable = false)
    private boolean archived;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Consequence consequence = (Consequence) o;
        return id == consequence.id &&
                archived == consequence.archived &&
                name.equals(consequence.name) &&
                probability == consequence.probability;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, probability, archived);
    }
}
