package org.iatoki.judgels.sandalphon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import javax.inject.Provider;
import judgels.service.jaxrs.JudgelsObjectMappers;
import play.libs.Json;

public class PlayObjectMapper implements Provider<ObjectMapper> {
    @Override
    public ObjectMapper get() {
        ObjectMapper mapper = JudgelsObjectMappers.configure(
                new ObjectMapper().registerModules(new Jdk8Module(), new GuavaModule()));

        Json.setObjectMapper(mapper);

        return mapper;
    }
}
