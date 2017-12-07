package judgels.jophiel.user.email;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailCodeGeneratorTests {
    @Test void generated_codes_are_nonempty() {
        assertThat(EmailCodeGenerator.newCode()).isNotEmpty();
    }

    @Test void generated_codes_are_unique() {
        assertThat(EmailCodeGenerator.newCode()).isNotEqualTo(EmailCodeGenerator.newCode());
    }

    @Test void generated_codes_are_lowercase() {
        String emailCode = EmailCodeGenerator.newCode();
        assertThat(emailCode).isEqualTo(emailCode.toLowerCase());
    }
}
