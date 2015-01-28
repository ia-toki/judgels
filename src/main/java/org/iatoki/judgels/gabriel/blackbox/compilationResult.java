package org.iatoki.judgels.gabriel.blackbox;

public final class CompilationResult {
    private final CompilationVerdict verdict;
    private final String output;

    public CompilationResult(CompilationVerdict verdict, String output) {
        this.verdict = verdict;
        this.output = output;
    }

    public CompilationVerdict getVerdict() {
        return verdict;
    }

    public String getOutput() {
        return output;
    }
}
