package com.bratchun.project.core.models;

import com.bratchun.project.core.injectors.TestInject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class TestInjectorModel {

    @TestInject
    private String injectedString;

    public String getTestInjectedString() { return injectedString; }

}
