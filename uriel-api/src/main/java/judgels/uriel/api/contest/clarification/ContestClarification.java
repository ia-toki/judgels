package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarification.class)
public interface ContestClarification {
    long getId();
    String getJid();
    String getUserJid();
    String getTopicJid();
    String getTitle();
    String getQuestion();
    ContestClarificationStatus getStatus();
    Instant getTime();

    Optional<String> getAnswer();
    Optional<String> getAnswererJid();
    Optional<Instant> getAnsweredTime();

    class Builder extends ImmutableContestClarification.Builder {}
}
