package org.iatoki.judgels.gabriel.blackbox.algorithms;

import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringResult;
import org.iatoki.judgels.gabriel.blackbox.ScoringVerdict;

import java.io.File;

public abstract class AbstractScorer implements Scorer {

    @Override
    public final ScoringResult score(File testCaseInput, File testCaseOutput) throws ScoringException {
        String scoringOutput = executeScoring(testCaseInput, testCaseOutput);

        String[] lines = scoringOutput.split("\n");

        if (lines.length == 0) {
            throw new ScoringException("Unknown scoring format: " + scoringOutput);
        }

        String verdict = lines[0];

        String score;
        if (lines.length > 1) {
            score = lines[1];
        } else {
            score = "";
        }

        switch (verdict) {
            case "AC":
                return new ScoringResult(ScoringVerdict.ACCEPTED, score);
            case "WA":
                return new ScoringResult(ScoringVerdict.WRONG_ANSWER, score);
            case "OK":
                return new ScoringResult(ScoringVerdict.OK, score);
            default:
                throw new ScoringException("Unknown scoring format: " + scoringOutput);
        }
    }

    protected abstract String executeScoring(File testCaseInput, File testCaseOutput) throws ScoringException;
}
