package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public final class TestSetVerdict {
    private final List<TestCaseVerdict> testCaseVerdicts;

    public TestSetVerdict(List<TestCaseVerdict> testCaseVerdicts) {
        this.testCaseVerdicts = testCaseVerdicts;
    }

    public List<TestCaseVerdict> getTestCaseVerdicts() {
        return testCaseVerdicts;
    }
}
