package xyz.vandijck.safety.backend.assembler.link;

import org.springframework.hateoas.RepresentationModel;

/**
 * Representation model for the root part of an endpoint, containing links to subsections of the application
 */
public class RootModel extends RepresentationModel<RootModel> {

    /**
     * Add a guarded link: gets added if guard allows it, otherwise doesn't get added.
     *
     * @param link The guarded link
     */
    public void add(GuardedLink link) {
        if (link.isGuard()) {
            this.add(link.getLink());
        }
    }
}
