package judgels.uriel.contest.style;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = IcpcContestStyleConfig.class)
public interface IcpcContestStyleConfig {
    LanguageRestriction getLanguageRestriction();

    @Value.Default
    default long getWrongSubmissionPenalty() {
        return 0;
    }
}
