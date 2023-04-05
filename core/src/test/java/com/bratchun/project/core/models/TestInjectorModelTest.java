package com.bratchun.project.core.models;


import com.bratchun.project.core.injectors.TestInjector;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.Injector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class TestInjectorModelTest {

    private TestInjector testInjector = new TestInjector();

    private final AemContext context = new AemContext();

    private Page page;

    private Resource resource;

    private TestInjectorModel model;

    @BeforeEach
    public void setUp () {
        context.registerService(Injector.class, testInjector);

        page = context.create().page("/context/testPage");

        resource = context.create().resource(page, "injector",
                "sling:resourceType", "/bratchun/test-injector");

        model = resource.adaptTo(TestInjectorModel.class);
    }


    @Test
    void getTestInjectedString() {
        String expected = "test injector";

        String actual = model.getTestInjectedString();

        assertEquals(expected, actual);
    }
}