package xyz.vandijck.safety.service;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;
import xyz.vandijck.safety.entity.consequence.Consequence;
import xyz.vandijck.safety.entity.consequence.ConsequenceInput;
import xyz.vandijck.safety.repository.ConsequenceRepository;

@Service
public class ConsequenceService {

    private static final UriTemplate CONSEQUENCE_URI_TEMPLATE = new UriTemplate("/consequences/{id}");

    @Autowired
    ConsequenceRepository consequenceRepository;

    public List<Consequence> findAll() {
        return consequenceRepository.findAll();
    }

    public Consequence findById(long id) {
        return consequenceRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Consequence not found with id: " + id) );
    }

    public Consequence findByURI(URI consequenceLevelURI) {
        return findById(Long.parseLong(CONSEQUENCE_URI_TEMPLATE.match(consequenceLevelURI.toASCIIString()).get("id")));
    }

    public Consequence create(ConsequenceInput consequenceInput) {
        return update(new Consequence(), consequenceInput);
    }

    public void update(long id, ConsequenceInput consequenceInput) {
        update(findById(id), consequenceInput);
    }

    public Consequence update(Consequence consequence, ConsequenceInput consequenceInput) {
        consequence.setName(consequenceInput.getName());
        consequence.setProbability(consequenceInput.getProbability());

        return consequenceRepository.save(consequence);
    }

    public void deleteById(long id) {
        consequenceRepository.delete(findById(id));
    }

}
