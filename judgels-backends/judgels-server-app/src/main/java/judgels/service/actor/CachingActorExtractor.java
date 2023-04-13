package judgels.service.actor;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.time.Duration;
import java.util.Optional;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.service.api.JudgelsServiceException;
import judgels.service.api.actor.ActorExtractor;
import judgels.service.api.actor.AuthHeader;

public final class CachingActorExtractor implements ActorExtractor {
    private final MyUserService myUserService;
    private final LoadingCache<AuthHeader, String> cache;

    public CachingActorExtractor(MyUserService myUserService) {
        this.myUserService = myUserService;
        this.cache = Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(5))
                .build(this::extractJidUncached);
    }

    public Optional<String> extractJid(AuthHeader authHeader) {
        return Optional.ofNullable(cache.get(authHeader));
    }

    private String extractJidUncached(AuthHeader authHeader) {
        try {
            return myUserService.getMyself(authHeader).getJid();
        } catch (JudgelsServiceException e) {
            if (e.getCode() != 401) {
                throw e;
            }
        }
        return null;
    }
}
