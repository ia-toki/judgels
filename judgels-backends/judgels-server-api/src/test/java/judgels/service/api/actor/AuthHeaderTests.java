package judgels.service.api.actor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class AuthHeaderTests {
    @Nested class valueOf {
        @Test void decodes_to_bearer_token() {
            AuthHeader authHeader = AuthHeader.valueOf("Bearer YWxpY2U6Ym9i");
            assertThat(authHeader.getBearerToken()).isEqualTo("YWxpY2U6Ym9i");
        }

        @Test void throws_if_has_incorrect_prefix() {
            assertThatThrownBy(() -> AuthHeader.valueOf("YWxpY2U6Ym9i"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Auth header must start with 'Bearer '");
        }
    }

    @Nested class of {
        @Test void encodes_to_auth_header() {
            AuthHeader authHeader = AuthHeader.of("YWxpY2U6Ym9i");
            assertThat(authHeader.toString()).isEqualTo("Bearer YWxpY2U6Ym9i");
        }
    }

    @Nested class toString {
        @Test void returns_correct_header() {
            AuthHeader authHeader = AuthHeader.valueOf("Bearer YWxpY2U6Ym9i");
            assertThat(authHeader.toString()).isEqualTo("Bearer YWxpY2U6Ym9i");
        }
    }
}
