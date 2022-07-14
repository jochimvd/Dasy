package xyz.vandijck.safety.entity.category;

import java.util.Set;
import javax.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;
import xyz.vandijck.safety.entity.consequence.Consequence;
import xyz.vandijck.safety.entity.observation.Observation;
import xyz.vandijck.safety.entity.severity.Severity;

@Data
@Entity
@Component
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_generator")
    private long id;

    private String name;

    @ManyToOne
    private Severity severity;

    @ManyToOne
    private Consequence consequence;

    @OneToMany(mappedBy = "category")
    private Set<Observation> observations;

}
