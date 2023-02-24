package com.bratchun.project.core.handlers;

import com.bratchun.project.core.servlets.ServiceResourceResolverProvider;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.version.VersionManager;
import java.util.Iterator;

import static com.day.cq.commons.jcr.JcrConstants.MIX_VERSIONABLE;

@Component(
        service = EventHandler.class,
        immediate = true,
        property = {
                EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC,
        }
)
public class PageVersioningPageChangeHandler implements EventHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String FILTER_REGEX = "/content/bratchun-project/us/en/[^/]*";

    @Reference
    private ServiceResourceResolverProvider resourceResolverProvider;

    @Override
    public void handleEvent(Event event) {
        try {
            PageEvent pageEvent = PageEvent.fromEvent(event);
            Iterator<PageModification> pageModificationIterator = pageEvent.getModifications();
            while (pageModificationIterator.hasNext()) {
                PageModification pageModification = pageModificationIterator.next();
                String pagePath = pageModification.getPath();

                if (!pagePath.matches(FILTER_REGEX)) {
                    continue;
                }

                String contextPath = pagePath + "/jcr:content";

                ResourceResolver resourceResolver = resourceResolverProvider.getResourceResolver();
                Session session = resourceResolver.adaptTo(Session.class);
                Resource resource = resourceResolver.getResource(contextPath);

                if (session != null && resource != null) {
                    VersionManager versionManager = session.getWorkspace().getVersionManager();

                    Node node = resource.adaptTo(Node.class);

                    if (!node.isNodeType(MIX_VERSIONABLE)) {
                        node.addMixin(MIX_VERSIONABLE);
                        session.save();
                    }

                    versionManager.checkin(contextPath);
                    session.save();
                    versionManager.checkout(contextPath);
                }
            }
        } catch (Exception error) {
            logger.error("Error during handling page event: {}", error.getMessage());
        }
    }
}
