package org.iatoki.judgels.gabriel.blackbox;

import java.util.Set;

public final class SampleTestCase {
    private final String input;
    private final String output;
    private final Set<Integer> subtaskNumbers;

    public SampleTestCase(String input, String output, Set<Integer> subtaskNumbers) {
        this.input = input;
        this.output = output;
        this.subtaskNumbers = subtaskNumbers;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }

    public Set<Integer> getSubtaskNumbers() {
        return subtaskNumbers;
    }
}
