package judgels.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.service.jaxrs.JudgelsObjectMappers;

@Module
public class JudgelsModule {
    private JudgelsModule() {}

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return JudgelsObjectMappers.OBJECT_MAPPER;
    }
}
