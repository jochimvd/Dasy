package xyz.vandijck.safety.backend.service;

import xyz.vandijck.safety.backend.entity.Site;
import xyz.vandijck.safety.backend.request.SiteSearchRequest;

public interface SiteService extends EntityService<Site, SiteSearchRequest> {

    Site findElseCreate(String name);

}
