package org.iatoki.judgels.gabriel.blackbox.algorithms;

import judgels.gabriel.api.ScoringException;
import judgels.gabriel.api.ScoringResult;
import judgels.gabriel.api.TestCaseVerdict;
import judgels.gabriel.scorers.TestCaseVerdictParser;
import org.iatoki.judgels.gabriel.blackbox.Scorer;

import java.io.File;

public abstract class AbstractScorer implements Scorer {
    private final TestCaseVerdictParser parser = new TestCaseVerdictParser();

    @Override
    public final ScoringResult score(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws ScoringException {
        String scoringOutput = executeScoring(testCaseInput, testCaseOutput, evaluationOutputFile);
        TestCaseVerdict testCaseVerdict = parser.parseOutput(scoringOutput);


        return new ScoringResult.Builder()
                .verdict(testCaseVerdict)
                .build();
    }

    protected abstract String executeScoring(File testCaseInput, File testCaseOutput, File evaluationOutputFile) throws ScoringException;
}
