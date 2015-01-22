package org.iatoki.judgels.gabriel;

public interface GradingHandler {
    void onComplete(String senderChannel, String submissionJid, GradingResult result);
}
