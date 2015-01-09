package org.iatoki.judgels.gabriel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public final class TestSet {

    private final List<TestCase> testCases;
    private final Set<Integer> subtasks;

    public TestSet(List<TestCase> testCases, Set<Integer> subtasks) {
        this.testCases = ImmutableList.copyOf(testCases);
        this.subtasks = ImmutableSet.copyOf(subtasks);
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public Set<Integer> getSubtasks() {
        return subtasks;
    }
}
