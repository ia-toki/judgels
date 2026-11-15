package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableTrainingGoogleAnalyticsSettings.class)
public interface TrainingGoogleAnalyticsSettings {
    Optional<String> getTrackingId();

    class Builder extends ImmutableTrainingGoogleAnalyticsSettings.Builder {}
}
