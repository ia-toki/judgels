package judgels.sandalphon.submission;

import static judgels.sandalphon.submission.programming.SubmissionUtils.checkGradingLanguageAllowed;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.common.collect.ImmutableSet;
import judgels.gabriel.api.LanguageRestriction;
import org.junit.jupiter.api.Test;

class SubmissionUtilsTests {
    @Test
    void check_grading_languages_allowed() {
        LanguageRestriction r0 = LanguageRestriction.noRestriction();
        LanguageRestriction r1 = LanguageRestriction.of(ImmutableSet.of("Cpp11", "Pascal"));

        assertThatCode(() -> checkGradingLanguageAllowed("Batch", "Cpp11", r0))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Batch", "Pascal", r0))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Batch", "Cpp11", r1))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("Batch", "Pascal", r1))
                .doesNotThrowAnyException();

        assertThatCode(() -> checkGradingLanguageAllowed("OutputOnly", "OutputOnly", r0))
                .doesNotThrowAnyException();
        assertThatCode(() -> checkGradingLanguageAllowed("OutputOnlyWithSubtasks", "OutputOnly", r1))
                .doesNotThrowAnyException();

        assertThatThrownBy(() -> checkGradingLanguageAllowed("Batch", "Python3", r1));
        assertThatThrownBy(() -> checkGradingLanguageAllowed("Batch", "OutputOnly", r0));
        assertThatThrownBy(() -> checkGradingLanguageAllowed("Batch", "OutputOnly", r1));
        assertThatThrownBy(() -> checkGradingLanguageAllowed("OutputOnly", "Cpp11", r0));
        assertThatThrownBy(() -> checkGradingLanguageAllowed("OutputOnly", "Cpp11", r1));
    }
}
