package judgels.service.actor;

import java.util.Optional;
import judgels.persistence.ActorProvider;

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
