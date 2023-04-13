package judgels.gabriel.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

class GradingResultDetailsTests {
    private static final ObjectMapper MAPPER = GabrielObjectMapper.getInstance();

    @Test
    void v1_deserializes_and_serializes() throws IOException {
        InputStream stream = GradingResultDetailsTests.class.getClassLoader()
                .getResourceAsStream("grading_result_details_v1.json");

        GradingResultDetails details = new GradingResultDetails.Builder()
                .putCompilationOutputs("source", "")
                .addTestDataResults(new TestGroupResult.Builder()
                        .id(0)
                        .addTestCaseResults(new TestCaseResult.Builder()
                                .verdict(Verdict.ACCEPTED)
                                .score("")
                                .executionResult(new SandboxExecutionResult.Builder()
                                        .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                                        .time(1)
                                        .memory(2304)
                                        .message(
                                                "time:0.001\ntime-wall:0.038\nmax-rss:3308\n"
                                                + "csw-voluntary:3\ncsw-forced:2\ncg-mem:2304\n")
                                        .build())
                                .addSubtaskIds(0)
                                .build())
                        .build())
                .addTestDataResults(new TestGroupResult.Builder()
                        .id(-1)
                        .addTestCaseResults(new TestCaseResult.Builder()
                                .verdict(Verdict.ACCEPTED)
                                .score("20.5")
                                .executionResult(new SandboxExecutionResult.Builder()
                                        .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                                        .time(1)
                                        .memory(2304)
                                        .message(
                                                "time:0.001\ntime-wall:0.061\nmax-rss:1272\n"
                                                + "csw-voluntary:5\ncsw-forced:3\ncg-mem:2304\n")
                                        .build())
                                .addSubtaskIds(-1)
                                .build())
                        .addTestCaseResults(new TestCaseResult.Builder()
                                .verdict(Verdict.RUNTIME_ERROR)
                                .score("0.0")
                                .executionResult(new SandboxExecutionResult.Builder()
                                        .status(SandboxExecutionStatus.KILLED_ON_SIGNAL)
                                        .time(1188)
                                        .memory(262144)
                                        .message(
                                                "exitsig:9\ntime:1.188\ntime-wall:1.454\nmax-rss:263168\n"
                                                + "csw-voluntary:3\ncsw-forced:596\ncg-mem:262144\n"
                                                + "status:SG\nmessage:Caught fatal signal 9\n")
                                        .build())
                                .addSubtaskIds(-1)
                                .build())
                        .build())
                .addSubtaskResults(new SubtaskResult.Builder()
                        .id(-1)
                        .verdict(Verdict.RUNTIME_ERROR)
                        .score(94)
                        .build())
                .build();

        GradingResultDetails details1 = MAPPER.readValue(stream, GradingResultDetails.class);
        assertThat(details1).isEqualTo(details);

        String str = MAPPER.writeValueAsString(details1);
        GradingResultDetails details2 = MAPPER.readValue(str, GradingResultDetails.class);
        assertThat(details2).isEqualTo(details);
    }

    @Test
    void v2_deserializes_and_serializes() throws IOException {
        InputStream stream = GradingResultDetailsTests.class.getClassLoader()
                .getResourceAsStream("grading_result_details_v2.json");

        GradingResultDetails details = new GradingResultDetails.Builder()
                .putCompilationOutputs("source", "")
                .addTestDataResults(new TestGroupResult.Builder()
                        .id(0)
                        .addTestCaseResults(new TestCaseResult.Builder()
                                .verdict(Verdict.ACCEPTED)
                                .score("")
                                .executionResult(new SandboxExecutionResult.Builder()
                                        .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                                        .time(1)
                                        .wallTime(38)
                                        .memory(2304)
                                        .build())
                                .addSubtaskIds(0)
                                .build())
                        .build())
                .addTestDataResults(new TestGroupResult.Builder()
                        .id(-1)
                        .addTestCaseResults(new TestCaseResult.Builder()
                                .verdict(Verdict.ACCEPTED)
                                .score("20.5")
                                .executionResult(new SandboxExecutionResult.Builder()
                                        .status(SandboxExecutionStatus.ZERO_EXIT_CODE)
                                        .time(1)
                                        .wallTime(61)
                                        .memory(2304)
                                        .build())
                                .addSubtaskIds(-1)
                                .build())
                        .addTestCaseResults(new TestCaseResult.Builder()
                                .verdict(Verdict.RUNTIME_ERROR)
                                .score("0.0")
                                .executionResult(new SandboxExecutionResult.Builder()
                                        .status(SandboxExecutionStatus.KILLED_ON_SIGNAL)
                                        .time(1188)
                                        .wallTime(1454)
                                        .memory(262144)
                                        .isKilled(true)
                                        .message("Caught fatal signal 9")
                                        .build())
                                .addSubtaskIds(-1)
                                .build())
                        .build())
                .addSubtaskResults(new SubtaskResult.Builder()
                        .id(-1)
                        .verdict(Verdict.RUNTIME_ERROR)
                        .score(94)
                        .build())
                .build();

        GradingResultDetails details1 = MAPPER.readValue(stream, GradingResultDetails.class);
        assertThat(details1).isEqualTo(details);

        String str = MAPPER.writeValueAsString(details1);
        GradingResultDetails details2 = MAPPER.readValue(str, GradingResultDetails.class);
        assertThat(details2).isEqualTo(details);
    }
}
