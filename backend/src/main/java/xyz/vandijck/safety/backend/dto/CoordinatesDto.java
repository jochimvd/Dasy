package xyz.vandijck.safety.backend.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Coordinates Data Transfer Object.
 * Usually used together with address.
 */
@Data
@Accessors(chain = true)
public class CoordinatesDto {
    private Double latitude;
    private Double longitude;
}
