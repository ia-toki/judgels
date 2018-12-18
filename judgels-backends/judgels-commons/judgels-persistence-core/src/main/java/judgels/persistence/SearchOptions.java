package judgels.persistence;

import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
public interface SearchOptions {
    Map<String, String> getTerms();

    class Builder extends ImmutableSearchOptions.Builder {}
}
