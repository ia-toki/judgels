package judgels.fs.local;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import judgels.fs.FsConfiguration;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLocalFsConfiguration.class)
public interface LocalFsConfiguration extends FsConfiguration {
    String getBaseDir();

    class Builder extends ImmutableLocalFsConfiguration.Builder {}
}
