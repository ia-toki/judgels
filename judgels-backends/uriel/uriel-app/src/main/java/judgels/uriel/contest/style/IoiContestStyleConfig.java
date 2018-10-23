package judgels.uriel.contest.style;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableIoiContestStyleConfig.class)
public interface IoiContestStyleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default boolean getUsingLastAffectingPenalty() {
        return false;
    }

    class Builder extends ImmutableIoiContestStyleConfig.Builder {}
}
