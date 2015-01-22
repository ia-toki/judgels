package org.iatoki.judgels.gabriel.steps;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils;
import org.iatoki.judgels.gabriel.ExecutionResult;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.Verdict;
import org.iatoki.judgels.gabriel.blackbox.Scorer;
import org.iatoki.judgels.gabriel.blackbox.ScoringException;
import org.iatoki.judgels.gabriel.blackbox.ScoringResult;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class CheckerSubtaskScorer implements Scorer {
    private final Sandbox sandbox;
    private List<String> scoringCommand;

    private static final String EVALUATION_OUTPUT_FILENAME = "_evaluation.out";
    private static final String SCORING_OUTPUT_FILENAME = "_scoring.out";

    public CheckerSubtaskScorer(Sandbox sandbox, List<String> scoringCommand) {
        this.sandbox = sandbox;
        this.scoringCommand = scoringCommand;
    }

    @Override
    public ScoringResult score(Map<String, File> evaluationOutputFiles, File testCaseInput, File testCaseOutput) throws ScoringException {
        sandbox.addFile(evaluationOutputFiles.get(EVALUATION_OUTPUT_FILENAME));
        sandbox.addFile(testCaseInput);
        sandbox.addFile(testCaseOutput);

        ImmutableList.Builder<String> scoringCommandWithArgs = ImmutableList.builder();
        scoringCommandWithArgs.addAll(scoringCommand);
        scoringCommandWithArgs.add(testCaseInput.getName());
        scoringCommandWithArgs.add(testCaseOutput.getName());
        scoringCommandWithArgs.add(EVALUATION_OUTPUT_FILENAME);

        sandbox.setStandardOutput(SCORING_OUTPUT_FILENAME);

        List<String> realScoringCommandWithArgs = scoringCommandWithArgs.build();
        ExecutionResult executionResult = sandbox.execute(realScoringCommandWithArgs);

        if (executionResult.getVerdict() != Verdict.OK) {
            throw new ScoringException(Joiner.on(" ").join(realScoringCommandWithArgs) + " resulted in " + executionResult.getVerdict());
        }

        String scoringOutput;
        try {
            File scoringOutputFile = sandbox.getFile(SCORING_OUTPUT_FILENAME);
            scoringOutput = FileUtils.readFileToString(scoringOutputFile);
        } catch (IOException e) {
            throw new ScoringException(e.getMessage());
        }

        if (scoringOutput.equals("OK")) {
            return new ScoringResult(Verdict.ACCEPTED, "100");
        } else if (scoringOutput.equals("WA")) {
            return new ScoringResult(Verdict.WRONG_ANSWER, "0");
        } else {
            throw new ScoringException("Bad scoring output format");
        }
    }
}
