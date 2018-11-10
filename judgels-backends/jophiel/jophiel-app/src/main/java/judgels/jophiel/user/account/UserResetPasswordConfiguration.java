package judgels.jophiel.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.jophiel.EmailTemplate;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserResetPasswordConfiguration.class)
public interface UserResetPasswordConfiguration {
    UserResetPasswordConfiguration DEFAULT = new UserResetPasswordConfiguration.Builder()
            .enabled(true)
            .requestEmailTemplate(new EmailTemplate.Builder()
                    .subject("Someone requested to reset your password")
                    .body("#{{emailCode}}#")
                    .build())
            .resetEmailTemplate(new EmailTemplate.Builder()
                    .subject("Your password has been reset")
                    .body("Your password has been reset")
                    .build())
            .build();

    boolean getEnabled();
    EmailTemplate getRequestEmailTemplate();
    EmailTemplate getResetEmailTemplate();

    class Builder extends ImmutableUserResetPasswordConfiguration.Builder {}
}
