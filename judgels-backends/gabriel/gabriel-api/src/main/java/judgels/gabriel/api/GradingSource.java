package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.File;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableGradingSource.class)
public interface GradingSource {
    Map<String, File> getSourceFiles();
    Map<String, File> getTestDataFiles();
    Map<String, File> getHelperFiles();

    class Builder extends ImmutableGradingSource.Builder {}
}
