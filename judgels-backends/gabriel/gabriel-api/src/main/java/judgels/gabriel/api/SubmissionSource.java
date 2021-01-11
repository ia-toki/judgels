package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionSource.class)
public interface SubmissionSource {
    String DEFAULT_KEY = "source";

    Map<String, SourceFile> getSubmissionFiles();

    default String asString() {
        Map<String, SourceFile> submissionFiles = getSubmissionFiles();
        StringBuilder source = new StringBuilder();
        for (Map.Entry<String, SourceFile> entry : submissionFiles.entrySet()) {
            if (submissionFiles.size() > 1) {
                source.append("------- ").append(entry.getKey()).append(" -------\n");
            }
            byte[] content = entry.getValue().getContent();
            source.append(content.length == 0 ? "N/A" : new String(content));
            if (submissionFiles.size() > 1) {
                source.append("\n");
            }
        }

        return source.toString();
    }

    class Builder extends ImmutableSubmissionSource.Builder {}
}
