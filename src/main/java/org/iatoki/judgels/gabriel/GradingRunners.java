package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRequest;
import org.iatoki.judgels.gabriel.blackbox.BlackBoxGradingRunner;

public final class GradingRunners {
    private GradingRunners() {
        // prevent instantiation
    }

    public static GradingRunner newRunner(String senderChannel, GradingRequest request, FakeSealtiel sealtiel) {
        if (request instanceof BlackBoxGradingRequest) {
            return new BlackBoxGradingRunner(senderChannel, (BlackBoxGradingRequest) request, sealtiel);
        } else {
            throw new IllegalStateException("Illegal grading request");
        }
    }
}
