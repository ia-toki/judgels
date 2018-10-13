package judgels.uriel.file;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import judgels.fs.local.LocalFsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableFileConfiguration.class)
public interface FileConfiguration {
    FileConfiguration DEFAULT = new Builder()
            .fs(new LocalFsConfiguration.Builder()
                    .build())
            .build();

    FsConfiguration getFs();

    class Builder extends ImmutableFileConfiguration.Builder {}
}
