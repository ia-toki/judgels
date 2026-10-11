package judgels.grading;

import static judgels.grading.aggregators.PointUtils.formatPoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import judgels.core.JudgelsObjectMappers;
import judgels.grading.api.GradingRequest;
import judgels.grading.api.GradingResponse;
import judgels.grading.api.GradingResult;
import judgels.grading.api.GradingResultDetails;
import judgels.grading.api.SandboxExecutionResult;
import judgels.grading.api.SandboxExecutionStatus;
import judgels.grading.api.SourceFile;
import judgels.grading.api.SubtaskResult;
import judgels.grading.api.TestCaseResult;
import judgels.grading.api.TestGroupResult;
import judgels.grading.api.Verdict;
import judgels.grading.api.Verdicts;

/**
 * Grades a submission without running it, for local development where no grader is available.
 *
 * <p>The verdict and the score are read from the name of the source file, as {@code <verdict>[-<score>]}:
 * {@code wa-40-solution.cpp} is Wrong Answer with a score of 40, and {@code ac-solution.cpp} is Accepted, whose score
 * is always 100. Any other name is accepted.
 *
 * <p>The details are made up to add up to that verdict and score: 10 test cases for an engine without subtasks, and
 * two subtasks of 10 test cases each for an engine with subtasks.
 */
public class FakeGrader {
    private static final ObjectMapper MAPPER = JudgelsObjectMappers.OBJECT_MAPPER;

    private static final Pattern FILENAME_PATTERN = Pattern.compile("^([a-z]+)(?:-(\\d+))?(?:[-.].*)?$");
    private static final Set<Verdict> FILENAME_VERDICTS = Set.of(
            Verdict.ACCEPTED,
            Verdict.OK,
            Verdict.WRONG_ANSWER,
            Verdict.TIME_LIMIT_EXCEEDED,
            Verdict.RUNTIME_ERROR,
            Verdict.COMPILATION_ERROR);

    private static final List<Verdict> FAILED_VERDICTS = List.of(
            Verdict.WRONG_ANSWER,
            Verdict.TIME_LIMIT_EXCEEDED,
            Verdict.RUNTIME_ERROR);

    private static final int TEST_CASES = 10;

    public GradingResponse grade(GradingRequest request, String gradingEngine) {
        Verdict verdict = Verdict.ACCEPTED;
        int score = 100;

        for (SourceFile file : request.getSubmissionSource().getSubmissionFiles().values()) {
            Matcher matcher = FILENAME_PATTERN.matcher(file.getName().toLowerCase());
            if (!matcher.matches()) {
                continue;
            }
            Verdict filenameVerdict = Verdicts.fromCode(matcher.group(1).toUpperCase());
            if (filenameVerdict == null || !FILENAME_VERDICTS.contains(filenameVerdict)) {
                continue;
            }

            verdict = filenameVerdict;
            if (verdict == Verdict.COMPILATION_ERROR) {
                score = 0;
            } else if (verdict != Verdict.ACCEPTED) {
                score = matcher.group(2) == null ? 0 : Math.min(Integer.parseInt(matcher.group(2)), 100);
            }
            break;
        }

        GradingResultDetails.Builder details = new GradingResultDetails.Builder();
        if (verdict == Verdict.COMPILATION_ERROR) {
            details.compilationOutputs(buildCompilationOutputs(request, "fake compilation error"));
        } else {
            details.compilationOutputs(buildCompilationOutputs(request, ""));
            details.addTestDataResults(new TestGroupResult.Builder()
                    .id(0)
                    .addTestCaseResults(buildTestCaseResult(Verdict.ACCEPTED, "", 0))
                    .build());

            if (gradingEngine.endsWith("WithSubtasks")) {
                addResultsWithSubtasks(details, verdict, score);
            } else {
                addResultsWithoutSubtasks(details, verdict, score);
            }
        }

        String detailsString;
        try {
            detailsString = MAPPER.writeValueAsString(details.build());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new GradingResponse.Builder()
                .gradingJid(request.getGradingJid())
                .result(new GradingResult.Builder()
                        .verdict(verdict)
                        .score(score)
                        .details(detailsString)
                        .build())
                .build();
    }

    // Each test case is worth the same; as many as the score allows are accepted, and the rest fail.
    private static void addResultsWithoutSubtasks(GradingResultDetails.Builder details, Verdict verdict, int score) {
        int testCaseScore = 100 / TEST_CASES;
        List<TestCaseResult> results = new ArrayList<>();

        if (verdict == Verdict.OK) {
            for (int i = 0; i < TEST_CASES; i++) {
                results.add(buildTestCaseResult(Verdict.OK, formatPoints(1.0 * score / TEST_CASES), -1));
            }
        } else {
            for (int i = 0; i < score / testCaseScore; i++) {
                results.add(buildTestCaseResult(Verdict.ACCEPTED, formatPoints(testCaseScore), -1));
            }
            if (score % testCaseScore > 0) {
                results.add(buildTestCaseResult(Verdict.OK, formatPoints(score % testCaseScore), -1));
            }
            for (int i = 0; results.size() < TEST_CASES; i++) {
                results.add(buildTestCaseResult(getFailedVerdict(verdict, i), formatPoints(0), -1));
            }
        }

        details.addTestDataResults(new TestGroupResult.Builder().id(-1).testCaseResults(results).build());
        details.addSubtaskResults(new SubtaskResult.Builder().id(-1).verdict(verdict).score(score).build());
    }

    // The first subtask gives the whole score, and the second one fails.
    private static void addResultsWithSubtasks(GradingResultDetails.Builder details, Verdict verdict, int score) {
        if (verdict == Verdict.ACCEPTED) {
            addSubtask(details, 1, Verdict.ACCEPTED, score / 2);
            addSubtask(details, 2, Verdict.ACCEPTED, score - score / 2);
        } else {
            addSubtask(details, 1, score > 0 ? Verdict.ACCEPTED : verdict, score);
            addSubtask(details, 2, verdict, 0);
        }
    }

    private static void addSubtask(GradingResultDetails.Builder details, int id, Verdict verdict, int score) {
        List<TestCaseResult> results = new ArrayList<>();

        if (verdict == Verdict.ACCEPTED) {
            for (int i = 0; i < TEST_CASES; i++) {
                results.add(buildTestCaseResult(Verdict.ACCEPTED, "*", id));
            }
        } else if (verdict == Verdict.OK) {
            for (int i = 0; i < TEST_CASES; i++) {
                results.add(buildTestCaseResult(Verdict.OK, formatPoints(0), id));
            }
        } else {
            // As in a real grading, the test cases after the first failed one are skipped.
            for (int i = 0; i < 3; i++) {
                results.add(buildTestCaseResult(Verdict.ACCEPTED, "*", id));
            }
            results.add(buildTestCaseResult(verdict, "X", id));
            while (results.size() < TEST_CASES) {
                results.add(buildTestCaseResult(Verdict.SKIPPED, "?", id));
            }
        }

        details.addTestDataResults(new TestGroupResult.Builder().id(id).testCaseResults(results).build());
        details.addSubtaskResults(new SubtaskResult.Builder().id(id).verdict(verdict).score(score).build());
    }

    // Alternates between the failed verdicts that are no worse than the given one, starting with it, so that the
    // worst test case verdict is still the given one.
    private static Verdict getFailedVerdict(Verdict verdict, int index) {
        int worst = FAILED_VERDICTS.indexOf(verdict);
        return FAILED_VERDICTS.get(worst - index % (worst + 1));
    }

    private static Map<String, String> buildCompilationOutputs(GradingRequest request, String output) {
        return request.getSubmissionSource().getSubmissionFiles().keySet().stream()
                .collect(Collectors.toMap(key -> key, key -> output));
    }

    private static TestCaseResult buildTestCaseResult(Verdict verdict, String score, int subtaskId) {
        TestCaseResult.Builder result = new TestCaseResult.Builder()
                .verdict(verdict)
                .score(score)
                .addSubtaskIds(subtaskId);

        if (verdict != Verdict.SKIPPED) {
            SandboxExecutionStatus status = SandboxExecutionStatus.ZERO_EXIT_CODE;
            if (verdict == Verdict.TIME_LIMIT_EXCEEDED) {
                status = SandboxExecutionStatus.TIMED_OUT;
            } else if (verdict == Verdict.RUNTIME_ERROR) {
                status = SandboxExecutionStatus.NONZERO_EXIT_CODE;
            }
            result.executionResult(new SandboxExecutionResult.Builder().status(status).time(10).memory(1024).build());
        }
        return result.build();
    }
}
