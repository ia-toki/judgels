package org.iatoki.judgels.gabriel.blackbox;

public final class TestCase {
    private final String input;
    private final String output;

    public TestCase(String input, String output) {
        this.input = input;
        this.output = output;
    }

    public String getInput() {
        return input;
    }

    public String getOutput() {
        return output;
    }
}
