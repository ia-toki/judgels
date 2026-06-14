package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAppSettingsUpdateData.class)
public interface AppSettingsUpdateData {
    Optional<String> getName();
    Optional<String> getSlogan();

    class Builder extends ImmutableAppSettingsUpdateData.Builder {}
}
