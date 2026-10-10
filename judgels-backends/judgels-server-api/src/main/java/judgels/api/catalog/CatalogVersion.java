package judgels.api.catalog;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.Instant;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableCatalogVersion.class)
public interface CatalogVersion {
    String getHash();
    String getUserJid();
    Instant getTime();
    String getTitle();
    String getDescription();

    class Builder extends ImmutableCatalogVersion.Builder {}
}
