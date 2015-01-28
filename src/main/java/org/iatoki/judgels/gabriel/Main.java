package org.iatoki.judgels.gabriel;

public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        init();

        GradingEngine engine = new GradingEngine();

        try {
            engine.run();
        } catch (InterruptedException e) {
            // nothing
        }
    }

    private static void init() {
        GabrielProperties.getInstance();
    }
}
