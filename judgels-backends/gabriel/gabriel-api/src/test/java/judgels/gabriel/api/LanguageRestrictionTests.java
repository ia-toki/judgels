package judgels.gabriel.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

class LanguageRestrictionTests {
    @Test
    void combine() {
        LanguageRestriction r1 = LanguageRestriction.of(new HashSet<>(Arrays.asList("Java8", "Cpp11", "Pascal")));
        LanguageRestriction r2 = LanguageRestriction.of(new HashSet<>(Arrays.asList("Cpp11", "Pascal", "Python3")));

        assertThat(LanguageRestriction.combine(r1, LanguageRestriction.of(new HashSet<>())).getAllowedLanguages())
                .containsOnly("Java8", "Cpp11", "Pascal");

        assertThat(LanguageRestriction.combine(LanguageRestriction.of(new HashSet<>()), r2).getAllowedLanguages())
                .containsOnly("Cpp11", "Pascal", "Python3");

        assertThat(LanguageRestriction.combine(r1, r2).getAllowedLanguages())
                .containsOnly("Cpp11", "Pascal");
    }
}
