package judgels.sandalphon.api.lesson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonInfo.class)
public interface LessonInfo {
    String getSlug();
    String getDefaultLanguage();
    Map<String, String> getTitlesByLanguage();

    class Builder extends ImmutableLessonInfo.Builder {}
}
