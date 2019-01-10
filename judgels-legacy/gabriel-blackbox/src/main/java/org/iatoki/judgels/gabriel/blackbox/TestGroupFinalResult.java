package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public final class TestGroupFinalResult {
    private final int id;
    private final List<TestCaseFinalResult> testCaseResults;

    public TestGroupFinalResult(int id, List<TestCaseFinalResult> testCaseResults) {
        this.id = id;
        this.testCaseResults = testCaseResults;
    }

    public int getId() {
        return id;
    }

    public List<TestCaseFinalResult> getTestCaseResults() {
        return testCaseResults;
    }
}
