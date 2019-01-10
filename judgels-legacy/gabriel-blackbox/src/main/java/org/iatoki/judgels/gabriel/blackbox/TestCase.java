package org.iatoki.judgels.gabriel.blackbox;

import java.util.Set;

public final class TestCase {
    private final String input;
    private final String output;
    private final Set<Integer> subtaskIds;

    public TestCase(String input, String output, Set<Integer> subtaskIds) {
        this.input = input;
        this.output = output;
        this.subtaskIds = subtaskIds;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public Set<Integer> getSubtaskIds() {
        return subtaskIds;
    }
}
