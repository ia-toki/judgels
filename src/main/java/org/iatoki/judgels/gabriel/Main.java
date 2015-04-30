package org.iatoki.judgels.gabriel;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

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
        Config config = ConfigFactory.load(Main.class.getClassLoader(), "conf/application.conf");

        GabrielProperties.buildInstance(config);
    }
}
