package org.iatoki.judgels.gabriel.steps;

import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringResult;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public final class SubtaskScorer implements Scorer {
    private final File tempDir;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";
    private static final String SCORING_OUTPUT_FILENAME = "_scoring.out";

    public SubtaskScorer(File tempDir) {
        this.tempDir = tempDir;
    }

    @Override
    public ScoringResult score(Map<String, File> evaluationOutputFiles, File testCaseInput, File testCaseOutput) throws ScoringException {
        File evaluationOutputFile = evaluationOutputFiles.get(EVALUATION_OUTPUT_FILENAME);
        File scoringOutputFile = new File(tempDir, SCORING_OUTPUT_FILENAME);

        String[] scoringCommand = new String[]{"/usr/bin/diff", testCaseOutput.getAbsolutePath(), evaluationOutputFile.getAbsolutePath(), ">", scoringOutputFile.getAbsolutePath()};

        int exitCode;
        try {
            exitCode = Runtime.getRuntime().exec(scoringCommand).waitFor();
        } catch (IOException | InterruptedException e) {
            throw new ScoringException(e.getMessage());
        }

        if (exitCode == 0) {
            return new ScoringResult(Verdict.ACCEPTED, "100");
        } else {
            return new ScoringResult(Verdict.WRONG_ANSWER, "0");
        }
    }
}
