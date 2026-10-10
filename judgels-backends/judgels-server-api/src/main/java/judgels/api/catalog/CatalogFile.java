package judgels.api.catalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCatalogFile.class)
public interface CatalogFile {
    String getName();
    long getSize();
    Instant getLastModifiedTime();

    class Builder extends ImmutableCatalogFile.Builder {}
}
