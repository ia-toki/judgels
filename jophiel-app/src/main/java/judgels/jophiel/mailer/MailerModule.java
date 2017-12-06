package judgels.jophiel.mailer;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module
public class MailerModule {
    private MailerConfiguration config;

    public MailerModule(MailerConfiguration config) {
        this.config = config;
    }

    @Provides
    @Singleton
    public Mailer mailer() {
        return new Mailer(config);
    }
}
