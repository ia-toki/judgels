package judgels.jophiel;

import dagger.Component;
import javax.inject.Singleton;

@Component
@Singleton
public interface JophielComponent {
    VersionResource versionResource();
}
