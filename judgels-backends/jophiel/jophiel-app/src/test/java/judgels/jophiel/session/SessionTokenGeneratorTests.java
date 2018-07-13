package judgels.jophiel.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionTokenGeneratorTests {
    @Test
    void generated_tokens_are_nonempty() {
        assertThat(SessionTokenGenerator.newToken()).isNotEmpty();
    }

    @Test
    void generated_tokens_are_unique() {
        assertThat(SessionTokenGenerator.newToken()).isNotEqualTo(SessionTokenGenerator.newToken());
    }
}
