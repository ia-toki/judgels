package judgels.jophiel.api.user.me;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutablePasswordUpdateData.class)
public interface PasswordUpdateData {
    String getOldPassword();
    String getNewPassword();

    static PasswordUpdateData of(String oldPassword, String newPassword) {
        return ImmutablePasswordUpdateData.builder()
                .oldPassword(oldPassword)
                .newPassword(newPassword)
                .build();
    }
}
