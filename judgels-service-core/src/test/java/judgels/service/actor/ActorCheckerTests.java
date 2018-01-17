package judgels.service.actor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.Optional;
import javax.ws.rs.NotAuthorizedException;
import judgels.service.api.actor.ActorExtractor;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ActorCheckerTests {
    private static final AuthHeader AUTH_HEADER = AuthHeader.of("token");
    private static final String ACTOR_JID = "JIDUSERactor";

    @Mock private ActorExtractor actorExtractor;
    private ActorChecker actorChecker;

    @BeforeEach void before() {
        initMocks(this);
        actorChecker = new ActorChecker(actorExtractor);
    }

    @Nested class check {
        @Nested class when_auth_header_is_valid {
            private String actorJid;

            @BeforeEach void before() {
                when(actorExtractor.extractJid(AUTH_HEADER)).thenReturn(Optional.of(ACTOR_JID));
                actorJid = actorChecker.check(AUTH_HEADER);
            }

            @Test void returns_actor_jid() {
                assertThat(actorJid).isEqualTo(ACTOR_JID);
            }

            @Test void sets_actor_jid() {
                assertThat(PerRequestActorProvider.getJid()).contains(ACTOR_JID);
            }
        }

        @Nested class when_auth_header_is_missing {
            @Test void throws_unauthorized() {
                assertThatExceptionOfType(NotAuthorizedException.class)
                        .isThrownBy(() -> actorChecker.check(null));
            }
        }

        @Nested class when_auth_header_is_invalid {
            @BeforeEach void before() {
                when(actorExtractor.extractJid(AUTH_HEADER)).thenReturn(Optional.empty());
            }

            @Test void throws_unauthorized() {
                assertThatExceptionOfType(NotAuthorizedException.class)
                        .isThrownBy(() -> actorChecker.check(AUTH_HEADER));
            }
        }
    }
}
