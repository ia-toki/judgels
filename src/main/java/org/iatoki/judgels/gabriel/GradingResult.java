package org.iatoki.judgels.gabriel;

public interface GradingResult {
    Verdict getVerdict();

    int getScore();

    String getMessage();

    String getDetails();
}
