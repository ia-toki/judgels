package judgels.gabriel.scorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.SandboxExecutionStatus;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;
import judgels.gabriel.helpers.TestCaseVerdictParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class TestCaseVerdictParserTests {
    private TestCaseVerdictParser parser;

    @BeforeEach
    void before() {
        parser = new TestCaseVerdictParser();
    }

    @Nested
    class ParseOutput {
        @Test
        void empty() {
            assertThatThrownBy(() -> parser.parseOutput(""))
                    .isInstanceOf(ScoringException.class)
                    .hasMessage("Expected: <code> on the first line");
        }

        @Test
        void unknown_verdict() {
            assertThatThrownBy(() -> parser.parseOutput("hokus pokus"))
                    .isInstanceOf(ScoringException.class)
                    .hasMessage("Unknown verdict: hokus pokus");
        }

        @Test
        void accepted() throws ScoringException {
            assertThat(parser.parseOutput("AC\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).build());
        }

        @Test
        void accepted_with_feedback() throws ScoringException {
            assertThat(parser.parseOutput("AC\na feedback\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.ACCEPTED).feedback("a feedback").build());
        }

        @Test
        void wa() throws ScoringException {
            assertThat(parser.parseOutput("WA\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.WRONG_ANSWER).build());
        }

        @Test
        void ok_points() throws ScoringException {
            assertThat(parser.parseOutput("OK\n70\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.OK).points(70.0).build());
        }

        @Test
        void ok_points_with_feedback() throws ScoringException {
            assertThat(parser.parseOutput("OK\n70\na feedback\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.OK).points(70.0).feedback("a feedback").build());
        }

        @Test
        void ok_points_with_feedback_on_same_line() throws ScoringException {
            assertThat(parser.parseOutput("OK\n70 a feedback\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.OK).points(70.0).feedback("a feedback").build());
        }

        @Test
        void ok_percentage() throws ScoringException {
            assertThat(parser.parseOutput("OK\n25%\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.OK).percentage(25).build());
        }

        @Test
        void ok_percentage_with_feedback() throws ScoringException {
            assertThat(parser.parseOutput("OK\n25%\na feedback\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.OK).percentage(25).feedback("a feedback").build());
        }

        @Test
        void ok_percentage_with_feedback_on_same_line() throws ScoringException {
            assertThat(parser.parseOutput("OK\n25% a feedback\n")).isEqualTo(
                    new TestCaseVerdict.Builder().verdict(Verdict.OK).percentage(25).feedback("a feedback").build());
        }

        @Test
        void ok_failed_empty_second_line() {
            assertThatThrownBy(() -> parser.parseOutput("OK\n"))
                    .isInstanceOf(ScoringException.class)
                    .hasMessage("Expected: <points> on the second line");
        }

        @Test
        void ok_failed_unknown_points_format() {
            assertThatThrownBy(() -> parser.parseOutput("OK\nabc\n"))
                    .isInstanceOf(ScoringException.class)
                    .hasMessage("Invalid <points> for OK: abc");
        }

        @Test
        void ok_failed_unknown_percentage_format() {
            assertThatThrownBy(() -> parser.parseOutput("OK\nabc%\n"))
                    .isInstanceOf(ScoringException.class)
                    .hasMessage("Invalid <percentage> for OK: abc%");
        }
    }

    @Nested
    class ParseExecutionResult {
        @Test
        void empty() {
            SandboxExecutionResult executionResult = new SandboxExecutionResult.Builder()
                    .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                    .time(2000)
                    .memory(65536)
                    .message("")
                    .build();
            assertThat(parser.parseExecutionResult(executionResult)).isEmpty();
        }

        @Test
        void tle() {
            SandboxExecutionResult executionResult = new SandboxExecutionResult.Builder()
                    .status(SandboxExecutionStatus.TIMED_OUT)
                    .time(3000)
                    .memory(65536)
                    .message("")
                    .build();
            assertThat(parser.parseExecutionResult(executionResult)).contains(
                    new TestCaseVerdict.Builder().verdict(Verdict.TIME_LIMIT_EXCEEDED).build());
        }

        @Test
        void rte() {
            SandboxExecutionResult executionResult = new SandboxExecutionResult.Builder()
                    .status(SandboxExecutionStatus.NONZERO_EXIT_CODE)
                    .time(0)
                    .memory(0)
                    .message("xxx")
                    .build();
            assertThat(parser.parseExecutionResult(executionResult)).contains(
                    new TestCaseVerdict.Builder().verdict(Verdict.RUNTIME_ERROR).build());
        }

        @Test
        void internal_error() {
            SandboxExecutionResult executionResult = new SandboxExecutionResult.Builder()
                    .status(SandboxExecutionStatus.INTERNAL_ERROR)
                    .time(-1)
                    .memory(-1)
                    .message("xxx")
                    .build();
            assertThat(parser.parseExecutionResult(executionResult)).contains(
                    new TestCaseVerdict.Builder().verdict(Verdict.INTERNAL_ERROR).build());
        }
    }
}
