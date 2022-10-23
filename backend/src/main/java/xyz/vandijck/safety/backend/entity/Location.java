package xyz.vandijck.safety.backend.entity;

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
@Table(name = "location")
@Indexed
@Data
public class Location implements UniqueEntity, Archivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "location_id", analyze = Analyze.NO, store = Store.NO, index = Index.YES)
    @SortableField(forField = "location_id")
    private long id;

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @Column(name = "name", nullable = false)
    @Analyzer(definition = "namelike")
    private String name;

    @Column(name = "description", length = 1023)
    private String description;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "archived", nullable = false)
    private boolean archived;

    @IndexedEmbedded
    @Embedded
    @Spatial
    private Coordinates coordinates;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return id == location.id &&
                archived == location.archived &&
                name.equals(location.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, archived);
    }
}
