package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRequest;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingWorker;
import org.iatoki.judgels.sealtiel.Sealtiel;

public final class GradingWorkers {
    private GradingWorkers() {
        // prevent instantiation
    }

    public static GradingWorker newWorker(String senderChannel, GradingRequest request, Sealtiel sealtiel, long messageId) {
        if (request instanceof BlackBoxGradingRequest) {
            return new BlackBoxGradingWorker(senderChannel, (BlackBoxGradingRequest) request, sealtiel, messageId);
        } else {
            throw new IllegalArgumentException("Illegal grading request");
        }
    }
}
