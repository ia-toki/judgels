package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationAnswerData.class)
public interface ContestClarificationAnswerData {
    String getAnswer();

    class Builder extends ImmutableContestClarificationAnswerData.Builder {}
}
