package judgels.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.setting.EmailTemplate;
import org.immutables.value.Value;

// Deprecated: Only read to seed the settings (see SettingCreator). Removed after 4.0.
@Deprecated
@Value.Immutable
@JsonDeserialize(as = ImmutableUserResetPasswordConfiguration.class)
public interface UserResetPasswordConfiguration {
    boolean getEnabled();
    EmailTemplate getRequestEmailTemplate();
    EmailTemplate getResetEmailTemplate();

    class Builder extends ImmutableUserResetPasswordConfiguration.Builder {}
}
