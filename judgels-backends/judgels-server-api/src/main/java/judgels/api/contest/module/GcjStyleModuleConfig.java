package judgels.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.grading.api.LanguageRestriction;
import org.immutables.value.Value;

@JsonTypeName("GCJ")
@Value.Style(passAnnotations = JsonTypeName.class)
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

    class Builder extends ImmutableGcjStyleModuleConfig.Builder {}
}
