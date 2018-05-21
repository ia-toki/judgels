package judgels.uriel.contest.style;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableContestStyleConfig.class)
public interface ContestStyleConfig {
    @JsonProperty("languageRestriction")
    LanguageRestriction getGradingLanguageRestriction();
}
