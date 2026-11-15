package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAppSettings.class)
public interface AppSettings {
    String DEFAULT_NAME = "Judgels";
    String DEFAULT_SLOGAN = "Programming Contest System";

    String getName();
    String getSlogan();
    Optional<String> getAnnouncement();

    class Builder extends ImmutableAppSettings.Builder {}
}
