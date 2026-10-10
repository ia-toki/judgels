package judgels.api.catalog.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonCreateData.class)
public interface LessonCreateData {
    String getSlug();
    String getAdditionalNote();
    String getInitialLanguage();

    class Builder extends ImmutableLessonCreateData.Builder {}
}
