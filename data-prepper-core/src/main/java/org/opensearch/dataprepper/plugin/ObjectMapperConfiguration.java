package org.opensearch.dataprepper.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.opensearch.dataprepper.model.types.ByteCount;
import org.opensearch.dataprepper.parser.ByteCountDeserializer;
import org.opensearch.dataprepper.parser.DataPrepperDurationDeserializer;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;
import java.time.Duration;
import java.util.Set;

/**
 * Application context for internal plugin framework beans.
 */
@Named
public class ObjectMapperConfiguration {
    static final Set<Class> TRANSLATE_VALUE_SUPPORTED_JAVA_TYPES = Set.of(
            String.class, Number.class, Long.class, Short.class, Integer.class, Double.class, Float.class,
            Boolean.class, Character.class);

    @Bean(name = "extensionPluginConfigObjectMapper")
    ObjectMapper extensionPluginConfigObjectMapper() {
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Duration.class, new DataPrepperDurationDeserializer());

        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(simpleModule);
    }

    @Bean(name = "pluginConfigObjectMapper")
    ObjectMapper pluginConfigObjectMapper(final VariableExpander variableExpander) {
        final SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Duration.class, new DataPrepperDurationDeserializer());
        simpleModule.addDeserializer(ByteCount.class, new ByteCountDeserializer());
        TRANSLATE_VALUE_SUPPORTED_JAVA_TYPES.stream().forEach(clazz -> simpleModule.addDeserializer(
                clazz, new DataPrepperScalarTypeDeserializer<>(variableExpander, clazz)));

        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .registerModule(simpleModule);
    }
}
