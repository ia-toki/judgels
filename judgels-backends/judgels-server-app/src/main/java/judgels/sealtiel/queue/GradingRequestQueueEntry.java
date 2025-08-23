package judgels.sealtiel.queue;

import judgels.gabriel.api.GradingRequest;
import org.immutables.value.Value;

@Value.Immutable
interface GradingRequestQueueEntry {
    GradingRequest getRequest();
    String getResponseQueueName();

    class Builder extends ImmutableGradingRequestQueueEntry.Builder {}
}
