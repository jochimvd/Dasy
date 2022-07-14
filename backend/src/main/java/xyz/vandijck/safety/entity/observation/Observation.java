package xyz.vandijck.safety.entity.observation;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import xyz.vandijck.safety.entity.category.Category;
import xyz.vandijck.safety.entity.location.Location;

@Entity
@Getter
@Setter
public class Observation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "observation_generator")
    private long id;

    @Column(name = "observation_key")
    private String key;

    private String observer;

    private String observerCompany;

    private ZonedDateTime observedAt;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    private String observedCompany;

    private boolean immediateDanger;

    private String type;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String description;

    private String actionsTaken;

    private String furtherActions;

    private Status status;


    @Column(updatable = false)
    @CreationTimestamp
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    private ZonedDateTime updatedAt;


    public int getWeekNr() {
        return observedAt.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
    }

    @Override
    public String toString() {
        return "Observation{key=" + key + "}";
    }

}
