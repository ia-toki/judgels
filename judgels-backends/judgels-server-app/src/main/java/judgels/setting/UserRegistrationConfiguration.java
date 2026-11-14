package judgels.setting;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.api.setting.EmailTemplate;
import org.immutables.value.Value;

// Deprecated: Only read to seed the settings (see SettingCreator). Removed after 4.0.
@Deprecated
@Value.Immutable
@JsonDeserialize(as = ImmutableUserRegistrationConfiguration.class)
public interface UserRegistrationConfiguration {
    boolean getEnabled();
    boolean getUseRecaptcha();
    EmailTemplate getActivationEmailTemplate();

    class Builder extends ImmutableUserRegistrationConfiguration.Builder {}
}
