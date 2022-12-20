package xyz.vandijck.safety.backend.service;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;

@Service
public class IndexService implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        reindexAll();
    }

    public void reindexAll() {
        try {
            FullTextEntityManager fullTextEntityManager =
                    Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
            fullTextEntityManager.createIndexer()
                    .purgeAllOnStart(true)
                    .startAndWait();
        } catch (InterruptedException e) {
            System.out.println(
                    "An error occurred trying to build the search index: " + e);
        }
    }
}
