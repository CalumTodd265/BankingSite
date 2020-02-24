package jooby.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Binder;
import com.mashape.unirest.http.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.jooby.Env;
import org.jooby.Jooby;

import javax.annotation.Nonnull;
import java.io.IOException;

public class UnirestHelper implements Jooby.Module {
    @Nonnull
    @Override
    public Config config() {
        return ConfigFactory.parseResources(getClass(), "unirest.properties");
    }

    @Override
    public void configure(Env env, Config conf, Binder binder) throws Throwable {
        Unirest.setObjectMapper(new ObjectMapper() {
            private com.fasterxml.jackson.databind.ObjectMapper jacksonObjectMapper
                    = new com.fasterxml.jackson.databind.ObjectMapper();

            public <T> T readValue(String value, Class<T> valueType) {
                try {
                    return jacksonObjectMapper.readValue(value, valueType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            public String writeValue(Object value) {
                try {
                    return jacksonObjectMapper.writeValueAsString(value);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
