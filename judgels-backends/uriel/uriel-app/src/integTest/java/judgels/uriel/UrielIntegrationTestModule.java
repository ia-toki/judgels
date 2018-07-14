package judgels.uriel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.remoting3.ext.jackson.ObjectMappers;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.service.jersey.JudgelsObjectMappers;

@Module
public class UrielIntegrationTestModule {
    private UrielIntegrationTestModule() {}

    @Provides
    @Singleton
    static ObjectMapper objectMapper() {
        return JudgelsObjectMappers.configure(ObjectMappers.newClientObjectMapper());
    }
}
