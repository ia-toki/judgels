package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableHomeSettings.class)
public interface HomeSettings {
    String DEFAULT_BANNER = "<h1>Welcome to Judgels</h1><h2>Please log in.</h2>";

    String getBanner();

    class Builder extends ImmutableHomeSettings.Builder {}
}
