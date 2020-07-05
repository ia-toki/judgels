package judgels.uriel.contest.log;

import java.time.Instant;
import java.util.Optional;
import java.util.Queue;
import javax.inject.Inject;
import javax.inject.Named;
import judgels.persistence.ActorProvider;
import judgels.uriel.api.contest.log.ContestLog;

public class ContestLogger {
    private final ActorProvider actorProvider;
    private final Queue<ContestLog> logQueue;

    @Inject
    public ContestLogger(ActorProvider actorProvider, @Named("ContestLogQueue") Queue<ContestLog> logQueue) {
        this.actorProvider = actorProvider;
        this.logQueue = logQueue;
    }

    public void log(String contestJid, String event) {
        log(contestJid, event, null, null);
    }

    public void log(String contestJid, String event, String object) {
        log(contestJid, event, object, null);
    }

    public void log(String contestJid, String event, String object, String problemJid) {
        if (!actorProvider.getJid().isPresent()) {
            return;
        }

        logQueue.add(new ContestLog.Builder()
                .contestJid(contestJid)
                .userJid(actorProvider.getJid().get())
                .event(event)
                .object(Optional.ofNullable(object))
                .problemJid(Optional.ofNullable(problemJid))
                .ipAddress(actorProvider.getIpAddress())
                .time(Instant.now())
                .build());
    }
}
