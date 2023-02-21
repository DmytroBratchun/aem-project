package com.bratchun.project.core.servlets.impl;

import com.bratchun.project.core.Constants;
import com.bratchun.project.core.servlets.ServiceResourceResolverProvider;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = ServiceResourceResolverProvider.class, immediate = true)
public class ServiceResourceResolverProviderImpl implements ServiceResourceResolverProvider {

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public ResourceResolver getResourceResolver() throws LoginException {
        return resourceResolverFactory.getServiceResourceResolver(Constants.ServiceUsers.BRATCHUN_SERVICE_USER_AUTH);
    }
}
