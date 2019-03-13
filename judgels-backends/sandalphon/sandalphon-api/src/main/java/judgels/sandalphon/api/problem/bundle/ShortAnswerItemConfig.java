package judgels.sandalphon.api.problem.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableShortAnswerItemConfig.class)
public interface ShortAnswerItemConfig extends ItemConfig {
    double getScore();
    double getPenalty();
    String getInputValidationRegex();
    Optional<String> getGradingRegex();

    class Builder extends ImmutableShortAnswerItemConfig.Builder {}
}
