package com.bratchun.project.core.servlets;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

public interface ServiceResourceResolverProvider {

    ResourceResolver getResourceResolver() throws LoginException;
}
