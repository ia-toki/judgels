package judgels.service.actor;

import static judgels.service.actor.Actors.GUEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import jakarta.ws.rs.NotAuthorizedException;
import java.util.Optional;
import judgels.jophiel.api.session.Session;
import judgels.jophiel.session.SessionStore;
import judgels.service.api.actor.AuthHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class ActorCheckerTests {
    private static final String BEARER_TOKEN = "token";
    private static final AuthHeader AUTH_HEADER = AuthHeader.of(BEARER_TOKEN);
    private static final String ACTOR_JID = "JIDUSERactor";

    @Mock private SessionStore sessionStore;
    private ActorChecker actorChecker;

    @BeforeEach
    void before() {
        initMocks(this);
        actorChecker = new ActorChecker(sessionStore);
    }

    @Nested
    class check {
        @Nested
        class when_auth_header_is_valid {
            private String actorJid;

            @BeforeEach
            void before() {
                when(sessionStore.getSessionByToken(BEARER_TOKEN)).thenReturn(Optional.of(Session.of(BEARER_TOKEN, ACTOR_JID)));
                actorJid = actorChecker.check(AUTH_HEADER);
            }

            @Test
            void returns_actor_jid() {
                assertThat(actorJid).isEqualTo(ACTOR_JID);
            }

            @Test
            void sets_actor_jid() {
                assertThat(PerRequestActorProvider.getJid()).contains(ACTOR_JID);
            }
        }

        @Nested
        class when_auth_header_is_missing {
            @Test
            void throws_unauthorized() {
                assertThatExceptionOfType(NotAuthorizedException.class)
                        .isThrownBy(() -> actorChecker.check((AuthHeader) null));
            }
        }

        @Nested
        class when_auth_header_is_invalid {
            @BeforeEach
            void before() {
                when(sessionStore.getSessionByToken(BEARER_TOKEN)).thenReturn(Optional.empty());
            }

            @Test
            void throws_unauthorized() {
                assertThatExceptionOfType(NotAuthorizedException.class)
                        .isThrownBy(() -> actorChecker.check(AUTH_HEADER));
            }
        }

        @Nested
        class when_auth_header_is_optional_and_empty {
            @Test
            void returns_guest() {
                assertThat(actorChecker.check(Optional.empty())).isEqualTo(GUEST);
            }
        }
    }
}
