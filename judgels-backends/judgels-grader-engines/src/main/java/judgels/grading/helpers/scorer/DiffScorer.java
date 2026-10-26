package judgels.grading.helpers.scorer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import judgels.grading.api.Scorer;
import judgels.grading.api.ScoringException;
import judgels.grading.api.ScoringResult;
import judgels.grading.api.TestCaseVerdict;
import judgels.grading.api.Verdict;

public class DiffScorer implements Scorer {
    @Override
    public ScoringResult score(File input, File output, File evaluationOutput) throws ScoringException {
        Verdict verdict;
        try (InputStream expected = new BufferedInputStream(new FileInputStream(output));
                InputStream actual = new BufferedInputStream(new FileInputStream(evaluationOutput))) {
            verdict = outputsMatch(expected, actual) ? Verdict.ACCEPTED : Verdict.WRONG_ANSWER;
        } catch (IOException e) {
            throw new ScoringException(e);
        }

        return new ScoringResult.Builder()
                .verdict(new TestCaseVerdict.Builder().verdict(verdict).build())
                .build();
    }

    private static boolean outputsMatch(InputStream expected, InputStream actual) throws IOException {
        while (true) {
            int expectedByte = readNonWhitespace(expected);
            int actualByte = readNonWhitespace(actual);
            if (expectedByte == -1 || actualByte == -1) {
                return expectedByte == actualByte;
            }

            while (true) {
                if (expectedByte != actualByte) {
                    return false;
                }
                expectedByte = expected.read();
                actualByte = actual.read();
                boolean expectedTokenEnd = expectedByte == -1 || isWhitespace(expectedByte);
                boolean actualTokenEnd = actualByte == -1 || isWhitespace(actualByte);
                if (expectedTokenEnd || actualTokenEnd) {
                    if (expectedTokenEnd != actualTokenEnd) {
                        return false;
                    }
                    break;
                }
            }
        }
    }

    private static int readNonWhitespace(InputStream stream) throws IOException {
        int value;
        do {
            value = stream.read();
        } while (isWhitespace(value));
        return value;
    }

    private static boolean isWhitespace(int value) {
        return value == ' ' || (value >= '\t' && value <= '\r');
    }
}
