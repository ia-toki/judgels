package judgels.uriel.api.contest.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

public interface StyleModuleConfig {
    @JsonProperty("languageRestriction")
    @Value.Default
    default LanguageRestriction getGradingLanguageRestriction() {
        return LanguageRestriction.noRestriction();
    }
}
