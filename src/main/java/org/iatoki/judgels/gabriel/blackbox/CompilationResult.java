package org.iatoki.judgels.gabriel.blackbox;

import java.util.Map;

public final class CompilationResult {
    private final CompilationVerdict verdict;
    private final Map<String, String> outputs;

    public CompilationResult(CompilationVerdict verdict, Map<String, String> output) {
        this.verdict = verdict;
        this.outputs = output;
    }

    public CompilationVerdict getVerdict() {
        return verdict;
    }

    public Map<String, String> getOutputs() {
        return outputs;
    }
}
