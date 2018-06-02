package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionSource.class)
public interface SubmissionSource {
    String DEFAULT_KEY = "source";

    Map<String, SourceFile> getSubmissionFiles();

    class Builder extends ImmutableSubmissionSource.Builder {}
}
