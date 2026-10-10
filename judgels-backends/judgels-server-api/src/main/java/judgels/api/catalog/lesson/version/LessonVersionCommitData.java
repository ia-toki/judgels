package judgels.api.catalog.lesson.version;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonVersionCommitData.class)
public interface LessonVersionCommitData {
    String getTitle();
    String getDescription();

    class Builder extends ImmutableLessonVersionCommitData.Builder {}
}
