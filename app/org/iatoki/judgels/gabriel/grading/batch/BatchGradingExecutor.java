package org.iatoki.judgels.gabriel.grading.batch;

import org.iatoki.judgels.gabriel.GradingExecutor;
import org.iatoki.judgels.gabriel.GradingRequest;
import org.iatoki.judgels.gabriel.GradingResult;
import org.iatoki.judgels.gabriel.blackbox.*;

public final class BatchGradingExecutor extends BlackBoxGradingExecutor {

    private CompilationExecutor compilationExecutor;
    private EvaluationExecutor evaluationExecutor;
    private ScoringExecutor scoringExecutor;
    private ScoreReducer scoreReducer;

    public BatchGradingExecutor() {
        this.compilationExecutor = new BatchCompilationExecutor();
        this.evaluationExecutor = new BatchEvaluationExecutor();
        this.scoringExecutor = new BatchScoringExecutor();
        this.scoreReducer = new BatchScoreReducer();
    }

    @Override
    protected CompilationExecutor getCompilationExecutor() {
        return compilationExecutor;
    }

    @Override
    protected EvaluationExecutor getEvaluationExecutor() {
        return evaluationExecutor;
    }

    @Override
    protected ScoringExecutor getScoringExecutor() {
        return scoringExecutor;
    }

    @Override
    protected ScoreReducer getScoreReducer() {
        return scoreReducer;
    }

    @Override
    public String getName() {
        return "Batch";
    }
}
