package xyz.vandijck.safety.entity.consequence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Data;


@Data
@Entity
public class Consequence {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "consequence_generator")
    private long id;

    private String name;

    private Double probability;

}
