package xyz.vandijck.safety.backend.assembler.link;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Data
@AllArgsConstructor
/**
 * A link container that has a boolean field, so it can be filtered later.
 */
public class GuardedLink {
    protected Link link;
    protected boolean guard;

    /**
     * Utility method for creating GuardedLinks
     *
     * @param bool The boolean to see if it has to be filtered
     * @param link The link
     * @return A GuardedLink instance
     */
    public static GuardedLink guard(boolean bool, Link link) {
        return new GuardedLink(link, bool);
    }

    /**
     * Filter a stream of GuardedLinks and get the links from them
     *
     * @param guardedLinkStream The guarded link stream
     * @return A filtered list of links
     */
    public static List<Link> filter(Stream<GuardedLink> guardedLinkStream) {
        return guardedLinkStream
                .filter(GuardedLink::isGuard)
                .map(GuardedLink::getLink)
                .collect(Collectors.toList());
    }
}
