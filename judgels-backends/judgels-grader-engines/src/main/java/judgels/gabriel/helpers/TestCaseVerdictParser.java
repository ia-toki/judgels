package judgels.gabriel.helpers;

import java.util.Optional;
import judgels.gabriel.api.SandboxExecutionResult;
import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.api.Verdict;

public class TestCaseVerdictParser {
    public TestCaseVerdict parseOutput(String output) throws ScoringException {
        String[] lines = output.split("\n");
        if (lines.length == 0 || output.equals("")) {
            throw new ScoringException("Expected: <code> on the first line");
        }

        Verdict verdict;
        Optional<Double> points = Optional.empty();
        Optional<Double> percentage = Optional.empty();
        Optional<String> feedback = Optional.empty();

        switch (lines[0]) {
            case "AC":
                verdict = Verdict.ACCEPTED;
                if (lines.length > 1) {
                    feedback = Optional.of(lines[1]);
                }
                break;
            case "WA":
                verdict = Verdict.WRONG_ANSWER;
                if (lines.length > 1) {
                    feedback = Optional.of(lines[1]);
                }
                break;
            case "OK":
                verdict = Verdict.OK;
                if (lines.length == 1) {
                    throw new ScoringException("Expected: <points> on the second line");
                }
                String[] tokens = lines[1].split(" ", 2);
                if (tokens.length == 0) {
                    throw new ScoringException("Invalid <points> for OK: " + lines[1]);
                }
                String result = tokens[0].trim();
                if (!result.isEmpty() && result.charAt(result.length() - 1) == '%') {
                    String percentageString = result.substring(0, result.length() - 1);
                    try {
                        percentage = Optional.of(Double.parseDouble(percentageString));
                    } catch (NumberFormatException e) {
                        throw new ScoringException("Invalid <percentage> for OK: " + result);
                    }
                } else {
                    try {
                        points = Optional.of(Double.parseDouble(result));
                    } catch (NumberFormatException e) {
                        throw new ScoringException("Invalid <points> for OK: " + result);
                    }
                }

                if (lines.length > 2) {
                    feedback = Optional.of(lines[2]);
                }
                if (tokens.length > 1) {
                    feedback = Optional.of(tokens[1]);
                }
                break;
            default:
                throw new ScoringException("Unknown verdict: " + output);
        }

        return new TestCaseVerdict.Builder()
                .verdict(verdict)
                .points(points)
                .percentage(percentage)
                .feedback(feedback)
                .build();
    }

    public Optional<TestCaseVerdict> parseExecutionResult(SandboxExecutionResult executionResult) {
        Optional<Verdict> verdict;
        switch (executionResult.getStatus()) {
            case ZERO_EXIT_CODE:
                verdict = Optional.empty();
                break;
            case TIMED_OUT:
                verdict = Optional.of(Verdict.TIME_LIMIT_EXCEEDED);
                break;
            case INTERNAL_ERROR:
                verdict = Optional.of(Verdict.INTERNAL_ERROR);
                break;
            default:
                verdict = Optional.of(Verdict.RUNTIME_ERROR);
        }

        return verdict.map(v -> new TestCaseVerdict.Builder().verdict(v).build());
    }
}
