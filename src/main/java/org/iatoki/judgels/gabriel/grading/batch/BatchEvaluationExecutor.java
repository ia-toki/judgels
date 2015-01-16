package org.iatoki.judgels.gabriel.grading.batch;

import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.ExecutionVerdict;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.EvaluationExecutor;
import org.iatoki.judgels.gabriel.blackbox.EvaluationVerdict;
import org.iatoki.judgels.gabriel.blackbox.GradingContext;

import java.io.File;
import java.util.List;
import java.util.Set;

public final class BatchEvaluationExecutor implements EvaluationExecutor {

    @Override
    public EvaluationVerdict evaluate(Sandbox sandbox, GradingContext context, File testCaseInput) {
        File sourceCode = context.getSourceFiles().entrySet().iterator().next().getValue();

        sandbox.addFile(testCaseInput);
        sandbox.setStandardInput(testCaseInput.getName());
        sandbox.setStandardOutput("contestant.out");

        List<String> evaluationCommand = context.getLanguage().getExecutionCommand(sourceCode.getName());

        ExecutionVerdict executionVerdict = sandbox.execute(evaluationCommand);
        Set<String> neededOutputFiles = ImmutableSet.of("contestant.out");

        return new EvaluationVerdict(executionVerdict, neededOutputFiles);
    }
}
