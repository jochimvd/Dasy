package xyz.vandijck.safety.backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.*;
import xyz.vandijck.safety.backend.entity.common.Status;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "observation")
@Indexed
@Data
public class Observation implements UniqueEntity, Archivable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    @Field(name = "observation_id", analyze = Analyze.NO, store = Store.NO, index = Index.YES)
    @SortableField(forField = "observation_id")
    private long id;

    @IndexedEmbedded
    @JsonBackReference("observerObservationReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "observer_id", referencedColumnName = "id", nullable = false)
    private User observer;

    @Field(normalizer = @Normalizer(definition = "dateSortNormalizer"))
    @SortableField
    @NotNull(message = "isNull")
    private ZonedDateTime observedAt;

    @IndexedEmbedded
    @JsonBackReference("siteObservationReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", referencedColumnName = "id", nullable = false)
    private Site site;

    @IndexedEmbedded
    @JsonBackReference("companyObservationReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", referencedColumnName = "id", nullable = false)
    private Company observedCompany;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @SortableField
    @Column(name = "immediate_danger", nullable = false)
    private Boolean immediateDanger;

    @IndexedEmbedded
    @JsonBackReference("typeObservationReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", referencedColumnName = "id", nullable = false)
    private Type type;

    @IndexedEmbedded
    @JsonBackReference("categoryObservationReference")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    private Category category;

    @Column(name = "description", length = 1023)
    @NotBlank(message = "isEmpty")
    private String description;

    @Column(name = "actions_taken", length = 1023)
    private String actionsTaken;

    @Column(name = "further_actions", length = 1023)
    private String furtherActions;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @SortableField
    @Enumerated(EnumType.STRING)
    @NotNull(message = "isNull")
    private Status status;

    @Field(analyze = Analyze.NO, store = Store.YES)
    @Column(name = "archived", nullable = false)
    private boolean archived;


    public String getKey() {
        String siteName = site.getName()
                .replaceAll("[^a-zA-Z]", "");

        return "SOR-" +
                siteName
                        .substring(0, Math.min(siteName.length(), 3))
                        .toUpperCase() +
                "-" + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Observation observation = (Observation) o;
        return id == observation.id &&
                archived == observation.archived &&
                observer.equals(observation.observer) &&
                site.equals(observation.site) &&
                immediateDanger == observation.immediateDanger;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, observer, site, immediateDanger, archived);
    }
}
