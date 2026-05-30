package tlx.mailer;

import dagger.Module;
import dagger.Provides;
import java.util.Optional;
import tlx.TlxScope;

@Module
public class MailerModule {
    private Optional<MailerConfiguration> config;

    public MailerModule(Optional<MailerConfiguration> config) {
        this.config = config;
    }

    @Provides
    @TlxScope
    public Optional<Mailer> mailer() {
        return config.map(Mailer::new);
    }
}
