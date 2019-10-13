package judgels.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RandomCodeGeneratorTests {
    @Test
    void generated_codes_are_nonempty() {
        assertThat(RandomCodeGenerator.newCode()).isNotEmpty();
    }

    @Test
    void generated_codes_are_unique() {
        assertThat(RandomCodeGenerator.newCode()).isNotEqualTo(RandomCodeGenerator.newCode());
    }
}
