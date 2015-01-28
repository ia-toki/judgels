package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;
import java.util.Set;

public final class TestGroup {
    private final List<TestCase> testCases;
    private final Set<Integer> subtaskNumbers;

    public TestGroup(List<TestCase> testCases, Set<Integer> subtaskNumbers) {
        this.testCases = testCases;
        this.subtaskNumbers = subtaskNumbers;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public Set<Integer> getSubtaskNumbers() {
        return subtaskNumbers;
    }
}
