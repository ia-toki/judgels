package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAppSettings.class)
public interface AppSettings {
    String getName();
    String getSlogan();

    class Builder extends ImmutableAppSettings.Builder {}
}
