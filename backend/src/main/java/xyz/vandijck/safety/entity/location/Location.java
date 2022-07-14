package xyz.vandijck.safety.entity.location;

import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import xyz.vandijck.safety.entity.observation.Observation;

@Entity
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_generator")
    private long id;

    private String name;

    @OneToMany(mappedBy = "location")
    private Set<Observation> observations;

}
