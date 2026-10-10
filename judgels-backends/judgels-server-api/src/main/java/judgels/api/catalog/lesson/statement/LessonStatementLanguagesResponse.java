package judgels.api.catalog.lesson.statement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonStatementLanguagesResponse.class)
public interface LessonStatementLanguagesResponse {
    Set<String> getEnabledLanguages();
    Set<String> getDisabledLanguages();
    String getDefaultLanguage();

    class Builder extends ImmutableLessonStatementLanguagesResponse.Builder {}
}
