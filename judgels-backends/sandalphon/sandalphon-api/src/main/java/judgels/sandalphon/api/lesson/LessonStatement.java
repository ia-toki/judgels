package judgels.sandalphon.api.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonStatement.class)
public interface LessonStatement {
    String getTitle();
    String getText();

    class Builder extends ImmutableLessonStatement.Builder {}
}
