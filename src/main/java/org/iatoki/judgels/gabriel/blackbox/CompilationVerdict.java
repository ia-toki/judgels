package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.ExecutionVerdict;

import java.util.Set;

public final class CompilationVerdict {
    private final ExecutionVerdict executionVerdict;
    private final Set<String> neededOutputFiles;

    public CompilationVerdict(ExecutionVerdict executionVerdict, Set<String> neededOutputFiles) {
        this.executionVerdict = executionVerdict;
        this.neededOutputFiles = neededOutputFiles;
    }

    public ExecutionVerdict getExecutionVerdict() {
        return executionVerdict;
    }

    public Set<String> getNeededOutputFiles() {
        return neededOutputFiles;
    }
}
