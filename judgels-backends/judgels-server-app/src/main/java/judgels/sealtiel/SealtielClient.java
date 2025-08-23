package judgels.sealtiel;

import jakarta.inject.Inject;
import judgels.gabriel.api.GradingRequest;
import judgels.sealtiel.queue.GradingRequestQueue;

public class SealtielClient {
    private final GradingRequestQueue gradingRequestQueue;

    @Inject
    public SealtielClient(GradingRequestQueue gradingRequestQueue) {
        this.gradingRequestQueue = gradingRequestQueue;
    }

    public void requestGrading(GradingRequest request, String responseQueueName) {
        gradingRequestQueue.push(request, responseQueueName);
    }
}
