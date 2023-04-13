package judgels.jophiel.api.user.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePasswordResetData.class)
public interface PasswordResetData {
    String getEmailCode();
    String getNewPassword();

    static PasswordResetData of(String emailCode, String newPassword) {
        return ImmutablePasswordResetData.builder()
                .emailCode(emailCode)
                .newPassword(newPassword)
                .build();
    }
}
