package org.iatoki.judgels.gabriel.blackbox;

import java.util.List;

public final class TestGroup {
    private final int id;
    private final List<TestCase> testCases;

    public TestGroup(int id, List<TestCase> testCases) {
        this.id = id;
        this.testCases = testCases;
    }

    public int getId() {
        return id;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }
}
