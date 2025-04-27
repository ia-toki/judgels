package judgels.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import judgels.JudgelsObjectMappers;

@Module
public class JudgelsModule {
    private JudgelsModule() {}

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return JudgelsObjectMappers.OBJECT_MAPPER;
    }
}
