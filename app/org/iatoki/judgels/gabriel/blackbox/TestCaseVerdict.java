package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionVerdict;

public class TestCaseVerdict {
    private final EvaluationVerdict evaluationExecutionVerdict;
    private final ScoringVerdict scoringVerdict;

    public TestCaseVerdict(EvaluationVerdict evaluationExecutionVerdict, ScoringVerdict scoringVerdict) {
        this.evaluationExecutionVerdict = evaluationExecutionVerdict;
        this.scoringVerdict = scoringVerdict;
    }

    public EvaluationVerdict getEvaluationExecutionVerdict() {
        return evaluationExecutionVerdict;
    }

    public ScoringVerdict getScoringVerdict() {
        return scoringVerdict;
    }
}
