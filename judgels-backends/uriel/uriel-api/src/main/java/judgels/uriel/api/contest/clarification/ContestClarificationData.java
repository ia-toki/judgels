package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationData.class)
public interface ContestClarificationData {
    String getTopicJid();
    String getTitle();
    String getQuestion();

    class Builder extends ImmutableContestClarificationData.Builder {}
}
