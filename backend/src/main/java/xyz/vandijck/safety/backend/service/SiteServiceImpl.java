package xyz.vandijck.safety.backend.service;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.vandijck.safety.backend.controller.exceptions.BadRequestException;
import xyz.vandijck.safety.backend.controller.exceptions.SiteNotFoundException;
import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.repository.SiteRepository;
import xyz.vandijck.safety.backend.request.DeleteRequest;
import xyz.vandijck.safety.backend.request.SiteSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class SiteServiceImpl implements SiteService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private UserService userService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private Patcher patcher;

    public SiteServiceImpl(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Override
    public SearchDTO<Site> findAll(SiteSearchRequest request) {
        return SearchHelper.findAll(request, entityManager, Site.class);
    }

    @Override
    public List<Site> findAll() {
        return siteRepository.findAll().stream().filter(site -> !site.isArchived()).collect(Collectors.toList());
    }

    @Override
    public Site findById(long id) {
        return getByIdOrThrow(id);
    }

    @Override
    public Site findElseCreate(String name) {
        Site site = siteRepository.findByName(name);
        if(site == null){
            site = siteRepository.save(new Site().setName(name));
        }
        return site;
    }

    @Override
    public Site save(Site site) {
        return siteRepository.save(site);
    }

    @Override
    public void deleteById(long id) {
        siteRepository.delete(getByIdOrThrow(id));
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(long id) {
        Site site = getByIdOrThrow(id);
        boolean deleted = true; // TODO check for observations on this site

        if (deleted) {
            siteRepository.delete(site);
        } else {
            site.setArchived(true);
            siteRepository.save(site);
        }
        return deleted;
    }

    @Override
    public Site patch(JsonPatch patch, long id) {
        Site site = getByIdOrThrow(id);
        Site patched = patcher.applyPatch(Site.class, patch, site);
        return save(patched);
    }

    @Override
    public Site getByIdOrThrow(long id) throws BadRequestException {
        Optional<Site> site = siteRepository.findById(id);
        if (site.isEmpty()) {
            throw new SiteNotFoundException();
        }
        return site.get();
    }

    @Override
    @Transactional
    public boolean archiveOrDeleteById(DeleteRequest request, long id) {
        userService.validateDeleteRequest(request);
        return archiveOrDeleteById(id);
    }

}
