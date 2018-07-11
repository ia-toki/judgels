package judgels.service.api.actor;

import java.util.Optional;

public interface ActorExtractor {
    Optional<String> extractJid(AuthHeader authHeader);
}
