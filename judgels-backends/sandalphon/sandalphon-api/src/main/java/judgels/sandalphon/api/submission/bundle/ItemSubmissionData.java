package judgels.sandalphon.api.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableItemSubmissionData.class)
public interface ItemSubmissionData {
    String getContainerJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();

    class Builder extends ImmutableItemSubmissionData.Builder {}
}
