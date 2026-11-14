package judgels.api.training.account.password;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAccountPasswordResetData.class)
public interface AccountPasswordResetData {
    String getEmailCode();
    String getNewPassword();

    static AccountPasswordResetData of(String emailCode, String newPassword) {
        return ImmutableAccountPasswordResetData.builder()
                .emailCode(emailCode)
                .newPassword(newPassword)
                .build();
    }
}
