package judgels.sealtiel.queue;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface Queue {
    QueueChannel createChannel() throws IOException, TimeoutException;
}
