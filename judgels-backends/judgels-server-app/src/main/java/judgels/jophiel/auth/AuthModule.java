package judgels.jophiel.auth;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;
import judgels.jophiel.auth.google.GoogleAuth;

@Module
public class AuthModule {
    private final Optional<AuthConfiguration> config;

    public AuthModule(Optional<AuthConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    public Optional<GoogleAuth> googleAuth() {
        return config.flatMap(AuthConfiguration::getGoogle).map(GoogleAuth::new);
    }
}
