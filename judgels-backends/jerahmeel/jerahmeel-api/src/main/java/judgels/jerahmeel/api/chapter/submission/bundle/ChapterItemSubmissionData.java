package judgels.jerahmeel.api.chapter.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterItemSubmissionData.class)
public interface ChapterItemSubmissionData {
    String getChapterJid();
    String getProblemJid();
    String getItemJid();
    String getAnswer();

    class Builder extends ImmutableChapterItemSubmissionData.Builder {}
}
