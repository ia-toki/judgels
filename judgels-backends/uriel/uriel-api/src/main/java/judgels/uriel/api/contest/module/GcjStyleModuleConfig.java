package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGcjStyleModuleConfig.class)
public interface GcjStyleModuleConfig extends StyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    @Override
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default int getWrongSubmissionPenalty() {
        return 4;
    }

    @Value.Default
    @Override
    default boolean hasPointsPerProblem() {
        return true;
    }

    class Builder extends ImmutableGcjStyleModuleConfig.Builder {}
}
