package judgels.persistence.actor;

import java.util.Optional;

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
