package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSessionSettings.class)
public interface SessionSettings {
    boolean getDisableLogout();
    int getMaxConcurrentSessionsPerUser();

    class Builder extends ImmutableSessionSettings.Builder {}
}
