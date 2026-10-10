package judgels.api.catalog.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonUpdateData.class)
public interface LessonUpdateData {
    String getSlug();
    String getAdditionalNote();

    class Builder extends ImmutableLessonUpdateData.Builder {}
}
