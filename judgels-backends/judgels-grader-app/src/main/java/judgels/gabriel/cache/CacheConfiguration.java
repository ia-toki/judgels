package judgels.gabriel.cache;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.file.Path;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCacheConfiguration.class)
public interface CacheConfiguration {
    Path getCachedBaseDataDir();

    class Builder extends ImmutableCacheConfiguration.Builder {}
}
