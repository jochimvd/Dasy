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
@Table(name = "reoccurrence")
@Indexed
@Data
public class Reoccurrence implements UniqueEntity, Archivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "reoccurrence_id", store = Store.YES, analyze = Analyze.NO)
    private long id;

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Column(name = "name", nullable = false)
    @Analyzer(definition = "namelike")
    private String name;

    @Column(name = "description", length = 1023)
    private String description;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "rate", nullable = false)
    private double rate;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "archived", nullable = false)
    private boolean archived;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reoccurrence reoccurrence = (Reoccurrence) o;
        return id == reoccurrence.id &&
                archived == reoccurrence.archived &&
                name.equals(reoccurrence.name) &&
                rate == reoccurrence.rate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, rate, archived);
    }
}
