package judgels.sealtiel.queue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import judgels.gabriel.api.GradingRequest;

public class GradingRequestQueue {
    private final Queue<GradingRequestQueueEntry> queue;

    public GradingRequestQueue() {
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public void push(GradingRequest request, String responseQueueName) {
        queue.offer(new GradingRequestQueueEntry.Builder()
                .request(request)
                .responseQueueName(responseQueueName)
                .build());
    }
}
