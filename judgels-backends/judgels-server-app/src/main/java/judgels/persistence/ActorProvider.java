package judgels.persistence;

import java.util.Optional;

public interface ActorProvider {
    Optional<String> getJid();
    Optional<String> getIpAddress();
}
