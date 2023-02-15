package com.bratchun.project.core.filters;

import com.bratchun.project.core.filters.wrappers.GenericResponseWrapper;
import com.day.image.Layer;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceRanking;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component(service = Filter.class,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
                EngineConstants.SLING_FILTER_PATTERN + "=" + "/content/we-retail/.*",
                EngineConstants.SLING_FILTER_EXTENSIONS + "=" + "jpeg",
                EngineConstants.SLING_FILTER_EXTENSIONS + "=" + "jpg",
                EngineConstants.SLING_FILTER_METHODS + "=" + "GET",
        })
@ServiceRanking(-700)
@Designate(ocd = ImageTransformationFilter.ImageTransformationFilterDesignate.class)
public class ImageTransformationFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<Consumer<Layer>> activeTransformers;

    private final Map<String, Consumer<Layer>> transformFunctions = Map.of(
            "rotate", img -> img.rotate(180),
            "grayscale", Layer::grayscale
    );


    @Activate
    @Modified
    public void activate(ImageTransformationFilterDesignate config) {
        List<String> transformerConfig = Arrays.asList(config.transformers());
        logger.info("Activate ImageTransformationFilter with config: {}", transformerConfig);

        activeTransformers = transformerConfig.stream()
                .filter(transformFunctions::containsKey)
                .map(transformFunctions::get)
                .collect(Collectors.toList());
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("ImageTransformationFilter inti");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) servletResponse;
        GenericResponseWrapper responseWrapper = new GenericResponseWrapper(slingResponse);

        filterChain.doFilter(servletRequest, responseWrapper);

        Layer layer = new Layer(responseWrapper.getInputStream());

        activeTransformers.forEach(transformer -> transformer.accept(layer));

        layer.write(layer.getMimeType(), 1, slingResponse.getOutputStream());
    }

    @Override
    public void destroy() {
        logger.info("ImageTransformationFilter destroy");
    }

    @ObjectClassDefinition
    @interface ImageTransformationFilterDesignate {

        @AttributeDefinition
        String[] transformers() default {};
    }
}
