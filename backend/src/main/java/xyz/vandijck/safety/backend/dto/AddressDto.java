package xyz.vandijck.safety.backend.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import xyz.vandijck.safety.backend.entity.Address;

@Data
@Accessors(chain = true)
public class AddressDto {

    private String streetWithNumber;
    private String city;
    private String zipCode;
    private String country;

    public Address toAddress(){
        return new Address()
                .setStreetWithNumber(streetWithNumber)
                .setCity(city)
                .setZipCode(zipCode)
                .setCountry(country);
    }
}
