package com.bratchun.project.core.listeners;

import com.bratchun.project.core.servlets.ServiceResourceResolverProvider;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.HashMap;
import java.util.Map;

import static com.bratchun.project.core.Constants.JobTopic.SAVE_PROPERTY;

@Component(immediate = true, service = EventListener.class)
public class PropertyRemoveListener implements EventListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ResourceResolver resourceResolver;
    private Session session;

    @Reference
    private ServiceResourceResolverProvider resourceResolverProvider;

    @Reference
    private JobManager jobManager;

    @Activate
    public void activate() {
        try {
            resourceResolver = resourceResolverProvider.getResourceResolver();
            session = resourceResolver.adaptTo(Session.class);
            ObservationManager observationManager = session.getWorkspace().getObservationManager();

            observationManager.addEventListener(
                    this,
                    Event.PROPERTY_REMOVED,
                    "/content/bratchun-project",
                    true,
                    null,
                    null,
                    false
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deactivate
    protected void deactivate() {
        if (session != null) {
            session.logout();
        }
    }

    @Override
    public void onEvent(EventIterator eventIterator) {
        while (eventIterator.hasNext()) {
            try {
                Event event = eventIterator.nextEvent();
                Map<String, Object> props = new HashMap<>();
                props.put("path", event.getPath());
                props.put("beforeValue", event.getInfo().get("beforeValue").toString());
                props.put("author", event.getUserID());

                jobManager.addJob(SAVE_PROPERTY, props);

                logger.info("Changes under: {}, info: {}", event.getPath(), event.getInfo());
            } catch (RepositoryException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
