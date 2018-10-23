package judgels.uriel.contest.style;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIcpcContestStyleConfig.class)
public interface IcpcContestStyleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default long getWrongSubmissionPenalty() {
        return 0;
    }

    class Builder extends ImmutableIcpcContestStyleConfig.Builder {}
}
