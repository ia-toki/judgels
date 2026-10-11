package judgels.grading;

import static judgels.grading.api.Verdict.ACCEPTED;
import static judgels.grading.api.Verdict.COMPILATION_ERROR;
import static judgels.grading.api.Verdict.OK;
import static judgels.grading.api.Verdict.RUNTIME_ERROR;
import static judgels.grading.api.Verdict.SKIPPED;
import static judgels.grading.api.Verdict.TIME_LIMIT_EXCEEDED;
import static judgels.grading.api.Verdict.WRONG_ANSWER;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import judgels.core.JudgelsObjectMappers;
import judgels.grading.api.GradingOptions;
import judgels.grading.api.GradingRequest;
import judgels.grading.api.GradingResult;
import judgels.grading.api.GradingResultDetails;
import judgels.grading.api.SourceFile;
import judgels.grading.api.SubmissionSource;
import judgels.grading.api.SubtaskResult;
import judgels.grading.api.TestCaseResult;
import judgels.grading.api.Verdict;
import org.junit.jupiter.api.Test;

class FakeGraderTests {
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;

    private final FakeGrader grader = new FakeGrader();

    @Test
    void reads_verdict_and_score_from_filename() {
        assertResult(grade("wa-40-solution.cpp", "Batch"), WRONG_ANSWER, 40);
        assertResult(grade("WA-40.cpp", "Batch"), WRONG_ANSWER, 40);
        assertResult(grade("tle.cpp", "Batch"), TIME_LIMIT_EXCEEDED, 0);
        assertResult(grade("ok-75-solution.py", "Batch"), OK, 75);
        assertResult(grade("ac-solution.cpp", "Batch"), ACCEPTED, 100);
        assertResult(grade("ac-40-solution.cpp", "Batch"), ACCEPTED, 100);
        assertResult(grade("ce-40-solution.cpp", "Batch"), COMPILATION_ERROR, 0);
    }

    @Test
    void accepts_any_other_filename() {
        assertResult(grade("solution.cpp", "Batch"), ACCEPTED, 100);
        assertResult(grade("walk-40.cpp", "Batch"), ACCEPTED, 100);
    }

    @Test
    void gives_test_case_results_adding_up_to_score_without_subtasks() throws IOException {
        GradingResultDetails details = getDetails(grade("wa-40-solution.cpp", "Batch"));

        assertThat(details.getTestDataResults()).hasSize(2);
        assertThat(getVerdicts(details, 1)).containsExactly(
                ACCEPTED, ACCEPTED, ACCEPTED, ACCEPTED,
                WRONG_ANSWER, WRONG_ANSWER, WRONG_ANSWER, WRONG_ANSWER, WRONG_ANSWER, WRONG_ANSWER);
        assertThat(getScores(details, 1)).containsExactly("10", "10", "10", "10", "0", "0", "0", "0", "0", "0");
        assertThat(details.getSubtaskResults()).containsExactly(subtaskResult(-1, WRONG_ANSWER, 40));
    }

    @Test
    void alternates_failed_verdicts_without_subtasks() throws IOException {
        GradingResultDetails details = getDetails(grade("rte-65-solution.cpp", "Batch"));

        assertThat(getVerdicts(details, 1)).containsExactly(
                ACCEPTED, ACCEPTED, ACCEPTED, ACCEPTED, ACCEPTED, ACCEPTED,
                OK,
                RUNTIME_ERROR, TIME_LIMIT_EXCEEDED, WRONG_ANSWER);
        assertThat(getScores(details, 1)).containsExactly("10", "10", "10", "10", "10", "10", "5", "0", "0", "0");
    }

    @Test
    void gives_passed_and_failed_subtasks_with_subtasks() throws IOException {
        GradingResultDetails details = getDetails(grade("wa-40-solution.cpp", "BatchWithSubtasks"));

        assertThat(details.getTestDataResults()).hasSize(3);
        assertThat(getVerdicts(details, 1)).hasSize(10).containsOnly(ACCEPTED);
        assertThat(getVerdicts(details, 2)).containsExactly(
                ACCEPTED, ACCEPTED, ACCEPTED, WRONG_ANSWER, SKIPPED, SKIPPED, SKIPPED, SKIPPED, SKIPPED, SKIPPED);
        assertThat(details.getSubtaskResults()).containsExactly(
                subtaskResult(1, ACCEPTED, 40),
                subtaskResult(2, WRONG_ANSWER, 0));
    }

    @Test
    void gives_only_passed_subtasks_when_accepted() throws IOException {
        GradingResultDetails details = getDetails(grade("ac-solution.cpp", "BatchWithSubtasks"));

        assertThat(details.getSubtaskResults()).containsExactly(
                subtaskResult(1, ACCEPTED, 50),
                subtaskResult(2, ACCEPTED, 50));
    }

    @Test
    void gives_no_test_data_results_on_compilation_error() throws IOException {
        GradingResultDetails details = getDetails(grade("ce-solution.cpp", "Batch"));

        assertThat(details.getCompilationOutputs()).containsEntry("source", "fake compilation error");
        assertThat(details.getTestDataResults()).isEmpty();
        assertThat(details.getSubtaskResults()).isEmpty();
    }

    private GradingResult grade(String filename, String gradingEngine) {
        GradingRequest request = new GradingRequest.Builder()
                .gradingJid("gradingJid")
                .problemJid("problemJid")
                .gradingLanguage("Cpp17")
                .submissionSource(new SubmissionSource.Builder()
                        .putSubmissionFiles("source", new SourceFile.Builder()
                                .name(filename)
                                .content("int main() {}".getBytes())
                                .build())
                        .build())
                .gradingLastUpdateTime(Instant.EPOCH)
                .gradingOptions(new GradingOptions.Builder().build())
                .build();

        return grader.grade(request, gradingEngine).getResult();
    }

    private static void assertResult(GradingResult result, Verdict verdict, int score) {
        assertThat(result.getVerdict()).isEqualTo(verdict);
        assertThat(result.getScore()).isEqualTo(score);
    }

    private static GradingResultDetails getDetails(GradingResult result) throws IOException {
        return MAPPER.readValue(result.getDetails(), GradingResultDetails.class);
    }

    private static List<Verdict> getVerdicts(GradingResultDetails details, int testGroupIndex) {
        return details.getTestDataResults().get(testGroupIndex).getTestCaseResults().stream()
                .map(TestCaseResult::getVerdict)
                .collect(Collectors.toList());
    }

    private static List<String> getScores(GradingResultDetails details, int testGroupIndex) {
        return details.getTestDataResults().get(testGroupIndex).getTestCaseResults().stream()
                .map(TestCaseResult::getScore)
                .collect(Collectors.toList());
    }

    private static SubtaskResult subtaskResult(int id, Verdict verdict, int score) {
        return new SubtaskResult.Builder().id(id).verdict(verdict).score(score).build();
    }
}
