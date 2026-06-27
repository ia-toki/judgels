package judgels.api.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSettingUpdateData.class)
public interface SettingUpdateData {
    Optional<AppSettings> getApp();
    Optional<SessionSettings> getSession();

    class Builder extends ImmutableSettingUpdateData.Builder {}
}
