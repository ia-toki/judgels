package judgels.persistence;

import java.util.Optional;
import judgels.core.PerRequestActorProvider;

public class JudgelsActorProvider implements ActorProvider {
    @Override
    public Optional<String> getJid() {
        return PerRequestActorProvider.getJid();
    }

    @Override
    public Optional<String> getIpAddress() {
        return PerRequestActorProvider.getIpAddress();
    }
}
