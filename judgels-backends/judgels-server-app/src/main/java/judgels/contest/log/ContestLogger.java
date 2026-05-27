package judgels.contest.log;

import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.time.Instant;
import java.util.Optional;
import java.util.Queue;
import judgels.api.contest.log.ContestLog;
import judgels.persistence.actor.ActorProvider;

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
