package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.persistence.api.dump.JudgelsDump;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationDump.class)
public interface ContestClarificationDump extends JudgelsDump {
    String getTopicJid();
    String getTitle();
    String getQuestion();
    Optional<String> getAnswer();

    class Builder extends ImmutableContestClarificationDump.Builder {}
}
