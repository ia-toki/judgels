package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@JsonTypeName("ICPC")
@Value.Style(passAnnotations = JsonTypeName.class)
@Value.Immutable
@JsonDeserialize(as = ImmutableIcpcStyleModuleConfig.class)
public interface IcpcStyleModuleConfig extends StyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    @Override
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }

    @Value.Default
    default long getWrongSubmissionPenalty() {
        return 20;
    }

    class Builder extends ImmutableIcpcStyleModuleConfig.Builder {}
}
