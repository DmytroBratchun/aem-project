package com.bratchun.project.core.consumers;

import com.bratchun.project.core.servlets.ServiceResourceResolverProvider;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.UUID;

import static com.bratchun.project.core.Constants.JobTopic.SAVE_PROPERTY;
import static com.day.cq.commons.jcr.JcrConstants.NT_UNSTRUCTURED;

@Component(service = JobConsumer.class, immediate = true,
    property=JobConsumer.PROPERTY_TOPICS + "=" + SAVE_PROPERTY)
public class SavePropertyJobConsumer implements JobConsumer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ServiceResourceResolverProvider resourceResolverProvider;

    @Override
    public JobResult process(Job job) {
        try {
            ResourceResolver resourceResolver = resourceResolverProvider.getResourceResolver();
            Resource resource = resourceResolver.getResource("/var/log/removedProperties");
            Node node = resource.adaptTo(Node.class);

            Node propertyLog = node.addNode(UUID.randomUUID().toString(), NT_UNSTRUCTURED);
            propertyLog.setProperty("path", (String) job.getProperty("path"));
            propertyLog.setProperty("author", (String) job.getProperty("author"));
            propertyLog.setProperty("time", job.getCreated().getTime().getTime());
            propertyLog.setProperty("beforeValue", (String) job.getProperty("beforeValue"));

            resourceResolver.commit();

            return JobResult.OK;
        } catch (Exception e) {
            logger.error("Failed process job", e);
            return JobResult.CANCEL;
        }
    }
}
