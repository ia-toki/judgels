package org.iatoki.judgels.gabriel;

public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        init();

        Grader grader = new Grader();

        try {
            grader.run();
        } catch (InterruptedException e) {
            // nothing
        }
    }

    private static void init() {
        GabrielProperties.getInstance();
    }
}
