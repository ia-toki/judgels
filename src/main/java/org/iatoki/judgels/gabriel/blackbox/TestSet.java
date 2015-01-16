package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;
import java.util.Set;

public final class TestSet {
    private final List<TestCase> testCases;
    private final Set<Integer> subtasks;

    public TestSet(List<TestCase> testCases, Set<Integer> subtasks) {
        this.testCases = testCases;
        this.subtasks = subtasks;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public Set<Integer> getSubtasks() {
        return subtasks;
    }
}
