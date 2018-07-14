package judgels.sandalphon;

import static judgels.sandalphon.SandalphonUtils.checkGradingLanguageAllowed;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import judgels.gabriel.api.LanguageRestriction;
import org.junit.jupiter.api.Test;

class SandalphonUtilsTests {
    @Test
    void check_grading_languages_allowed() {
        LanguageRestriction r0 = LanguageRestriction.noRestriction();
        LanguageRestriction r1 = LanguageRestriction.of(ImmutableSet.of("Cpp11", "Pascal"));
        LanguageRestriction r2 = LanguageRestriction.of(ImmutableSet.of("Cpp11", "Python3"));
        LanguageRestriction r3 = LanguageRestriction.noRestriction();

        assertThatCode(() -> checkGradingLanguageAllowed("Cpp11", ImmutableList.of(r0)))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Pascal", ImmutableList.of(r0)))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Pascal", ImmutableList.of(r0, r1)))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Cpp11", ImmutableList.of(r0, r1)))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Cpp11", ImmutableList.of(r1, r2)))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Cpp11", ImmutableList.of(r1, r2, r3)))
                .doesNotThrowAnyException();


        assertThatThrownBy(() -> checkGradingLanguageAllowed("Python3", ImmutableList.of(r0, r1)));
        assertThatThrownBy(() -> checkGradingLanguageAllowed("Python3", ImmutableList.of(r1)));
        assertThatThrownBy(() -> checkGradingLanguageAllowed("Python3", ImmutableList.of(r1, r2)));
    }
}
