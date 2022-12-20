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
@Table(name = "type")
@Indexed
@Data
public class Type implements UniqueEntity, Archivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "type_id", store = Store.YES, analyze = Analyze.NO)
    private long id;

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Column(name = "name", nullable = false)
    @Analyzer(definition = "namelike")
    private String name;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "notify", nullable = false)
    private boolean notify;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "archived", nullable = false)
    private boolean archived;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return id == type.id &&
                archived == type.archived &&
                name.equals(type.name) &&
                notify == type.notify;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, notify, archived);
    }
}
