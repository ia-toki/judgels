package org.iatoki.judgels.gabriel;

public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        init();

        GradingEngine engine = new GradingEngine(args[0]);

        try {
            engine.run();
        } catch (InterruptedException e) {
            // nothing
        }
    }

    private static void init() {
        GabrielConfig.getInstance();
    }
}
