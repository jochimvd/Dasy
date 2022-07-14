package xyz.vandijck.safety.service;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;
import xyz.vandijck.safety.entity.location.Location;
import xyz.vandijck.safety.entity.location.LocationInput;
import xyz.vandijck.safety.repository.LocationRepository;

@Service
public class LocationService {

    private static final UriTemplate LOCATION_URI_TEMPLATE = new UriTemplate("/locations/{id}");

    @Autowired
    LocationRepository locationRepository;

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location findById(long id) {
        return locationRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id) );
    }

    public Location findByURI(URI locationURI) {
        return findById(Long.parseLong(LOCATION_URI_TEMPLATE.match(locationURI.toASCIIString()).get("id")));
    }

    public Location create(LocationInput locationInput) {
        return update(new Location(), locationInput);
    }

    public void update(long id, LocationInput locationInput) {
        update(findById(id), locationInput);
    }

    public Location update(Location location, LocationInput locationInput) {
        location.setName(locationInput.getName());

        return locationRepository.save(location);
    }

    public void deleteById(long id) {
        locationRepository.delete(findById(id));
    }

}
