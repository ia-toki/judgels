package judgels.jerahmeel.api.chapter.submission.bundle;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import java.util.Map;
import judgels.jerahmeel.api.chapter.submission.ChapterSubmissionConfig;
import judgels.jophiel.api.profile.Profile;
import judgels.sandalphon.api.problem.bundle.ItemType;
import judgels.sandalphon.api.submission.bundle.ItemSubmission;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableAnswerSummaryResponse.class)
public interface AnswerSummaryResponse {
    Profile getProfile();
    ChapterSubmissionConfig getConfig();
    Map<String, List<String>> getItemJidsByProblemJid();
    Map<String, ItemSubmission> getSubmissionsByItemJid();
    Map<String, ItemType> getItemTypesMap();
    Map<String, String> getProblemAliasesMap();
    Map<String, String> getProblemNamesMap();

    class Builder extends ImmutableAnswerSummaryResponse.Builder {}
}
