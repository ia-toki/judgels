package org.iatoki.judgels.gabriel;

public interface GradingExecutor {
    String getName();

    String grade(String requestAsJson);
}
