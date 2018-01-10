package judgels.jophiel.user.avatar;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import judgels.fs.local.LocalFsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableUserAvatarConfiguration.class)
public interface UserAvatarConfiguration {
    UserAvatarConfiguration DEFAULT = new Builder()
            .fs(new LocalFsConfiguration.Builder()
                    .build())
            .build();

    FsConfiguration getFs();

    class Builder extends ImmutableUserAvatarConfiguration.Builder {}
}
