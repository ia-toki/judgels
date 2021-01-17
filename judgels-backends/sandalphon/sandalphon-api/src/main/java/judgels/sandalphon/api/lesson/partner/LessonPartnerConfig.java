package judgels.sandalphon.api.lesson.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonPartnerConfig.class)
public interface LessonPartnerConfig {
    boolean getIsAllowedToUpdateLesson();

    boolean getIsAllowedToUpdateStatement();
    boolean getIsAllowedToUploadStatementResources();
    Set<String> getAllowedStatementLanguagesToView();
    Set<String> getAllowedStatementLanguagesToUpdate();
    boolean getIsAllowedToManageStatementLanguages();

    boolean getIsAllowedToViewVersionHistory();
    boolean getIsAllowedToRestoreVersionHistory();

    class Builder extends ImmutableLessonPartnerConfig.Builder {}
}
