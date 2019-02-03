package judgels.service.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableSet;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import judgels.service.api.client.BasicAuthHeader;
import judgels.service.api.client.Client;
import org.junit.jupiter.api.Test;

class ClientCheckerTests {
    private static final Client CLIENT_1 = Client.of("JID-1", "secret-1");
    private static final Client CLIENT_2 = Client.of("JID-2", "secret-2");
    private static final Client CLIENT_3 = Client.of("JID-3", "secret-1");

    private ClientChecker clientChecker = new ClientChecker(ImmutableSet.of(CLIENT_1, CLIENT_2));

    @Test
    void passes_if_client_exists() {
        assertThat(clientChecker.check(BasicAuthHeader.of(CLIENT_1))).isEqualTo(CLIENT_1);
        assertThat(clientChecker.check(BasicAuthHeader.of(CLIENT_2))).isEqualTo(CLIENT_2);
    }

    @Test
    void fails_for_missing_header() {
        assertThatExceptionOfType(NotAuthorizedException.class)
                .isThrownBy(() -> clientChecker.check(null));
    }

    @Test
    void fails_for_wrong_jid() {
        BasicAuthHeader authHeader = BasicAuthHeader.of(Client.of("wrong-jid", CLIENT_1.getSecret()));
        assertThatExceptionOfType(ForbiddenException.class)
                .isThrownBy(() -> clientChecker.check(authHeader));
    }

    @Test
    void fails_for_wrong_secret() {
        BasicAuthHeader authHeader = BasicAuthHeader.of(Client.of(CLIENT_1.getJid(), "wrong-secret"));
        assertThatExceptionOfType(ForbiddenException.class)
                .isThrownBy(() -> clientChecker.check(authHeader));
    }

    @Test
    void fails_for_wrong_client() {
        assertThatExceptionOfType(ForbiddenException.class)
                .isThrownBy(() -> clientChecker.check(BasicAuthHeader.of(CLIENT_3)));
    }
}
