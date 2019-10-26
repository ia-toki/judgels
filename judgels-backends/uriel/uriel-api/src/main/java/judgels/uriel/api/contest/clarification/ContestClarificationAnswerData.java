package judgels.uriel.api.contest.clarification;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestClarificationAnswerData.class)
public interface ContestClarificationAnswerData {
    String getAnswer();
    Optional<Boolean> getIsEdit();

    class Builder extends ImmutableContestClarificationAnswerData.Builder {}
}
