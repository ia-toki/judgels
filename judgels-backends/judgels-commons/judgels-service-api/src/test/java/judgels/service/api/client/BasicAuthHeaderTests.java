package judgels.service.api.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BasicAuthHeaderTests {
    @Nested class valueOf {
        @Test void decodes_to_client() {
            BasicAuthHeader authHeader = BasicAuthHeader.valueOf("Basic YWxpY2U6Ym9i");
            assertThat(authHeader.getClient()).isEqualTo(Client.of("alice", "bob"));
        }

        @Test void throws_if_has_incorrect_prefix() {
            assertThatThrownBy(() -> BasicAuthHeader.valueOf("YWxpY2U6Ym9i"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Auth header must start with 'Basic '");
        }

        @Test void throws_if_has_invalid_base64_format() {
            assertThatThrownBy(() -> BasicAuthHeader.valueOf("Basic bogus"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Auth header must contain base64-encoded jid:secret");
        }

        @Test void throws_if_has_invalid_base64_credentials() {
            assertThatThrownBy(() -> BasicAuthHeader.valueOf("Basic Ym9ndXM="))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Auth header must contain base64-encoded jid:secret");
        }
    }

    @Nested class of {
        @Test void encodes_to_auth_header() {
            BasicAuthHeader authHeader = BasicAuthHeader.of(Client.of("alice", "bob"));
            assertThat(authHeader.toString()).isEqualTo("Basic YWxpY2U6Ym9i");
        }
    }

    @Nested class toString {
        @Test void returns_correct_header() {
            BasicAuthHeader authHeader = BasicAuthHeader.valueOf("Basic YWxpY2U6Ym9i");
            assertThat(authHeader.toString()).isEqualTo("Basic YWxpY2U6Ym9i");
        }
    }
}
