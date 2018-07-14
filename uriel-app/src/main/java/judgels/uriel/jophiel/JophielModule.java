package judgels.uriel.jophiel;

import com.palantir.remoting3.clients.UserAgent;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import judgels.jophiel.api.user.MyService;
import judgels.jophiel.api.user.UserService;
import judgels.service.jaxrs.JaxRsClients;

@Module
public class JophielModule {
    private final JophielConfiguration config;

    public JophielModule(JophielConfiguration config) {
        this.config = config;
    }


    @Provides
    @Singleton
    UserService userService(UserAgent agent) {
        return JaxRsClients.create(UserService.class, config.getBaseUrl(), agent);
    }

    @Provides
    @Singleton
    MyService myService(UserAgent agent) {
        return JaxRsClients.create(MyService.class, config.getBaseUrl(), agent);
    }
}
