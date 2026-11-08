package judgels.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGoogleAnalyticsConfiguration.class)
public interface GoogleAnalyticsConfiguration {
    String getTrackingId();

    class Builder extends ImmutableGoogleAnalyticsConfiguration.Builder {}
}
