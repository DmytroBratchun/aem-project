package com.bratchun.project.core;

import org.apache.sling.api.resource.ResourceResolverFactory;

import java.util.Collections;
import java.util.Map;

public final class Constants {

    public static final class ServiceUsers {
        public static final String BRATCHUN_SERVICE_USER_NAME = "bratchun-service-user";
        public static final Map<String, Object> BRATCHUN_SERVICE_USER_AUTH =
                Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, BRATCHUN_SERVICE_USER_NAME);
    }
}
