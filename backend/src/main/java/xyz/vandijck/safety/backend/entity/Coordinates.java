package xyz.vandijck.safety.backend.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;

import javax.persistence.Embeddable;

@Embeddable
@Accessors(chain = true)
@Data
@Spatial
public class Coordinates implements org.hibernate.search.spatial.Coordinates {
    @Latitude
    private Double latitude;

    @Longitude
    private Double longitude;
}
