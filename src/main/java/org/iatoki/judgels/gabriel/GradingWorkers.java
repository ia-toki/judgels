package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRequest;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingWorker;

public final class GradingWorkers {
    private GradingWorkers() {
        // prevent instantiation
    }

    public static GradingWorker newWorker(String senderChannel, GradingRequest request, FakeSealtiel sealtiel) {
        if (request instanceof BlackBoxGradingRequest) {
            return new BlackBoxGradingWorker(senderChannel, (BlackBoxGradingRequest) request, sealtiel);
        } else {
            throw new IllegalArgumentException("Illegal grading request");
        }
    }
}
