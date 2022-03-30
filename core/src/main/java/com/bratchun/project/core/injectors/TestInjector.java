package com.bratchun.project.core.injectors;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.osgi.framework.Constants.SERVICE_RANKING;

@Component(service = Injector.class)
public class TestInjector implements Injector {

    @Override
    public String getName() {
        return "test-injector";
    }

    @Override
    public Object getValue(Object object, String s, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (annotatedElement.isAnnotationPresent(TestInject.class)) {
            Resource resource = (Resource) object;
            if (resource != null) {
                ValueMap valueMap = resource.adaptTo(ValueMap.class);
                return valueMap.values().toString();
            }
        }

        return "test-injector";
    }
}
