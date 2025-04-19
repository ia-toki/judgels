package judgels.jophiel.mailer;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.util.Optional;

@Module
public class MailerModule {
    private Optional<MailerConfiguration> config;

    public MailerModule(Optional<MailerConfiguration> config) {
        this.config = config;
    }

    @Provides
    @Singleton
    public Optional<Mailer> mailer() {
        return config.map(Mailer::new);
    }
}
