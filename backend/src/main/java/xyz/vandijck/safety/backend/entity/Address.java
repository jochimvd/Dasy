package xyz.vandijck.safety.backend.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.search.annotations.*;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

/**
 * An entity containing all the fields of an address
 */
@Embeddable
@Accessors(chain = true)
@Data
public class Address implements Serializable
{
    private static final long serialVersionUID = -1375058899098613462L;
    @Field(store = Store.NO, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @NotBlank
    @SortableField
    private String streetWithNumber;

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @NotBlank
    @Analyzer(definition = "namelike")
    private String city;

    @NotBlank
    @Field(analyze = Analyze.NO, store = Store.YES)
    @SortableField
    private String zipCode;

    @Field(store = Store.YES, normalizer = @Normalizer(definition = "asciiSortNormalizer"))
    @SortableField
    @NotBlank
    @Analyzer(definition = "namelike")
    private String country;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return streetWithNumber.equals(address.streetWithNumber) &&
                city.equals(address.city) &&
                zipCode.equals(address.zipCode) &&
                country.equals(address.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streetWithNumber, city, zipCode, country);
    }
}
