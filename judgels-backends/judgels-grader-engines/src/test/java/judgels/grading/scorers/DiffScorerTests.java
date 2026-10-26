package judgels.grading.scorers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import judgels.grading.api.ScoringException;
import judgels.grading.api.Verdict;
import judgels.grading.helpers.scorer.DiffScorer;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiffScorerTests {
    private Path directory;

    @BeforeEach
    void before() throws IOException {
        directory = Files.createTempDirectory("diff-scorer");
    }

    @AfterEach
    void after() throws IOException {
        FileUtils.deleteDirectory(directory.toFile());
    }

    @Test
    void accepts_matching_large_tokens() throws IOException, ScoringException {
        String output = "01".repeat(100_000);
        assertThat(score(output, output)).isEqualTo(Verdict.ACCEPTED);
    }

    @Test
    void rejects_different_large_tokens() throws IOException, ScoringException {
        String prefix = "01".repeat(100_000);
        assertThat(score(prefix + "0", prefix + "1")).isEqualTo(Verdict.WRONG_ANSWER);
    }

    @Test
    void rejects_large_tokens_of_different_lengths() throws IOException, ScoringException {
        String output = "01".repeat(100_000);
        assertThat(score(output, output + "1")).isEqualTo(Verdict.WRONG_ANSWER);
    }

    @Test
    void ignores_whitespace_between_tokens() throws IOException, ScoringException {
        assertThat(score(" \t\r\n1  2\t3\r\n", "1\n2 3")).isEqualTo(Verdict.ACCEPTED);
    }

    @Test
    void preserves_token_boundaries() throws IOException, ScoringException {
        assertThat(score("1 01", "10 1")).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(score("101", "1 01")).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(score("1 01", "101")).isEqualTo(Verdict.WRONG_ANSWER);
    }

    @Test
    void rejects_missing_or_extra_tokens() throws IOException, ScoringException {
        assertThat(score("1 0", "1")).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(score("1", "1 0")).isEqualTo(Verdict.WRONG_ANSWER);
    }

    @Test
    void accepts_empty_outputs() throws IOException, ScoringException {
        assertThat(score("", "")).isEqualTo(Verdict.ACCEPTED);
        assertThat(score(" \t\r\n", "")).isEqualTo(Verdict.ACCEPTED);
        assertThat(score("", " \t\r\n")).isEqualTo(Verdict.ACCEPTED);
    }

    @Test
    void rejects_empty_output_against_a_token() throws IOException, ScoringException {
        assertThat(score("", "1")).isEqualTo(Verdict.WRONG_ANSWER);
        assertThat(score("1", "")).isEqualTo(Verdict.WRONG_ANSWER);
    }

    @Test
    void reports_missing_files_as_scoring_errors() throws IOException {
        Path output = Files.writeString(directory.resolve("output.txt"), "1");
        Path missing = directory.resolve("missing.txt");
        DiffScorer scorer = new DiffScorer();

        assertThatThrownBy(() -> scorer.score(null, missing.toFile(), output.toFile()))
                .isInstanceOf(ScoringException.class)
                .hasCauseInstanceOf(IOException.class);
        assertThatThrownBy(() -> scorer.score(null, output.toFile(), missing.toFile()))
                .isInstanceOf(ScoringException.class)
                .hasCauseInstanceOf(IOException.class);
        assertThatThrownBy(() -> scorer.score(null, missing.toFile(), missing.toFile()))
                .isInstanceOf(ScoringException.class)
                .hasCauseInstanceOf(IOException.class);
    }

    private Verdict score(String expected, String actual) throws IOException, ScoringException {
        Path output = Files.writeString(directory.resolve("output.txt"), expected);
        Path evaluationOutput = Files.writeString(directory.resolve("evaluation-output.txt"), actual);
        return new DiffScorer()
                .score(null, output.toFile(), evaluationOutput.toFile())
                .getVerdict().getVerdict();
    }
}
