package org.iatoki.judgels.gabriel;

import java.io.File;
import java.io.IOException;

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
        GabrielConfig.getInstance();
    }
}
