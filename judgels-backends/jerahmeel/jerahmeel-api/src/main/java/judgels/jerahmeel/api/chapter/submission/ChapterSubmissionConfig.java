package judgels.jerahmeel.api.chapter.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableChapterSubmissionConfig.class)
public interface ChapterSubmissionConfig {
    boolean getCanManage();

    class Builder extends ImmutableChapterSubmissionConfig.Builder {}
}
