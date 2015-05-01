package org.iatoki.judgels.gabriel;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public final class Main {
    private Main() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        init();

        int threads;
        if (args.length > 0) {
            threads = Integer.parseInt(args[0]);
        } else {
            threads = getDefaultThreadsCount();
        }

        Grader grader = new Grader(threads);

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

    private static int getDefaultThreadsCount() {
        return Math.max(1, (Runtime.getRuntime().availableProcessors() - 1) * 1 * 2);
    }
}
