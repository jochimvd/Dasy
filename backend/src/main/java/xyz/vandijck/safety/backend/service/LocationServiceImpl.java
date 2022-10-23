package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.LocationNotFoundException;
import xyz.vandijck.safety.backend.entity.Location;
import xyz.vandijck.safety.backend.repository.LocationRepository;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.LocationSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public SearchDTO<Location> findAll(LocationSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Location.class);
    }

    @Override
    public List<Location> findAll() {
        return locationRepository.findAll().stream().filter(location -> !location.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Location findById(long id) {
        return getByIdOrThrow(id);
    }


    @Override
    public Location save(Location location) {
        return locationRepository.save(location);
    }

    @Override
    public void deleteById(long id) {
        locationRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Location location = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for observations on this location

        if (deleted) {
            locationRepository.delete(location);
        } else {
            location.setArchived(true);
            locationRepository.save(location);
        }
        return deleted;
    }

    @Override
    public Location patch(JsonPatch patch, long id) {
        Location location = getByIdOrThrow(id);
        Location patched = patcher.applyPatch(Location.class, patch, location);
        return save(patched);
    }

    @Override
    public Location getByIdOrThrow(long id) throws BadRequestException {
        Optional<Location> location = locationRepository.findById(id);
        if (location.isEmpty()) {
            throw new LocationNotFoundException();
        }
        return location.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
