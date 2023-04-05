package com.bratchun.project.core.injectors;

import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor2;
import org.apache.sling.models.spi.injectorspecific.StaticInjectAnnotationProcessorFactory;
import org.osgi.service.component.annotations.Component;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

import static org.osgi.framework.Constants.SERVICE_RANKING;

@Component(service = Injector.class, property = {SERVICE_RANKING + "=" + 1005})
public class TestInjector implements Injector, StaticInjectAnnotationProcessorFactory {

    @Override
    public String getName() {
        return "test-injector";
    }

    @Override
    public Object getValue(Object object, String s, Type type, AnnotatedElement annotatedElement, DisposalCallbackRegistry disposalCallbackRegistry) {
        if (annotatedElement.isAnnotationPresent(TestInject.class)) {
            return "test injector";
        }

        return null;
    }

    @Override
    public InjectAnnotationProcessor2 createAnnotationProcessor(AnnotatedElement annotatedElement) {
        return null;
    }
}
