package org.iatoki.judgels.gabriel;

public interface GradingResult {
    Verdict getVerdict();

    String getMessage();

    int getScore();

    String getDetailsAsJson();
}
