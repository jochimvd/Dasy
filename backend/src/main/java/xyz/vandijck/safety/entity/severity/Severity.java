package xyz.vandijck.safety.entity.severity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Severity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "severity_generator")
    private long id;

    private String name;

    private Double level;

}
