package xyz.vandijck.safety.service;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;
import xyz.vandijck.safety.entity.severity.Severity;
import xyz.vandijck.safety.entity.severity.SeverityInput;
import xyz.vandijck.safety.repository.SeverityRepository;

@Service
public class SeverityService {

    private static final UriTemplate SEVERITY_URI_TEMPLATE = new UriTemplate("/severities/{id}");

    @Autowired
    SeverityRepository severityRepository;

    public List<Severity> findAll() {
        return severityRepository.findAll();
    }

    public Severity findById(long id) {
        return severityRepository.findById(id)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Severity not found with id: " + id) );
    }

    public Severity findByURI(URI severityLevelURI) {
        return findById(Long.parseLong(SEVERITY_URI_TEMPLATE.match(severityLevelURI.toASCIIString()).get("id")));
    }

    public Severity create(SeverityInput severityInput) {
        return update(new Severity(), severityInput);
    }

    public void update(long id, SeverityInput severityInput) {
        update(findById(id), severityInput);
    }

    public Severity update(Severity severity, SeverityInput severityInput) {
        severity.setName(severityInput.getName());
        severity.setLevel(severityInput.getLevel());

        return severityRepository.save(severity);
    }

    public void deleteById(long id) {
        severityRepository.delete(findById(id));
    }

}
