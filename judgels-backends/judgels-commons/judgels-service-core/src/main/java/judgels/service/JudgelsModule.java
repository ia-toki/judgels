package judgels.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.remoting3.ext.jackson.ObjectMappers;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.service.jersey.JudgelsObjectMappers;

@Module
public class JudgelsModule {
    private JudgelsModule() {}

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(ObjectMappers.newClientObjectMapper());
    }
}
