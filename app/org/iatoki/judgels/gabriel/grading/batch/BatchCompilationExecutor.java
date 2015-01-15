package org.iatoki.judgels.gabriel.grading.batch;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.iatoki.judgels.gabriel.ExecutionVerdict;
import org.iatoki.judgels.gabriel.Sandbox;
import org.iatoki.judgels.gabriel.blackbox.CompilationExecutor;
import org.iatoki.judgels.gabriel.blackbox.CompilationVerdict;
import org.iatoki.judgels.gabriel.blackbox.GradingContext;

import java.io.File;
import java.util.List;
import java.util.Set;

public final class BatchCompilationExecutor implements CompilationExecutor {

    @Override
    public CompilationVerdict compile(Sandbox sandbox, GradingContext context) {
        File sourceCode = context.getSourceFiles().entrySet().iterator().next().getValue();
        sandbox.addFile(sourceCode);

        String executableFilename = context.getLanguage().getExecutableFilename(sourceCode.getName());
        List<String> compilationCommand = context.getLanguage().getCompilationCommand(sourceCode.getName());

        ExecutionVerdict executionVerdict = sandbox.execute(compilationCommand);
        Set<String> neededOutputFiles = ImmutableSet.of(executableFilename);

        return new CompilationVerdict(executionVerdict, neededOutputFiles);
    }
}
