package eu.heronnet.module.gui.model.metadata;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import eu.heronnet.core.model.Field;

/**
 * @author edoardocausarano
 */
@Component
public class FieldProcessorFactory {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FieldProcessorFactory.class);
    @Inject
    ApplicationContext applicationContext;
    private HashMap<String, MetadataProcessor> strategiesByMimeType;

    @PostConstruct
    public void scan() {
        Map<String, Object> strategies = applicationContext.getBeansWithAnnotation(FieldProcessorStrategy.class);
        strategiesByMimeType = new HashMap<>(strategies.size());
        strategies.values().stream().filter(o -> MetadataProcessor.class.isAssignableFrom(o.getClass())).forEach(o -> {
            FieldProcessorStrategy annotation = o.getClass().getDeclaredAnnotation(FieldProcessorStrategy.class);
            strategiesByMimeType.put(annotation.mimeType(), (MetadataProcessor) o);
        });
    }

    public MetadataProcessor getProcessor(String mimeType) {
        return strategiesByMimeType.getOrDefault(mimeType, new MetadataProcessor() {
            @Override
            public List<Field> process(File file) throws IOException {
                logger.warn("Unknown mime-type={} processor requested for file={}", mimeType, file);
                return Collections.emptyList();
            }
        });
    }
}
