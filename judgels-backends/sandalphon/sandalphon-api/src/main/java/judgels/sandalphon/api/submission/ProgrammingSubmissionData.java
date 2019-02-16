package judgels.sandalphon.api.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProgrammingSubmissionData.class)
public interface ProgrammingSubmissionData {
    String getProblemJid();
    String getContainerJid();
    String getGradingLanguage();
    Optional<LanguageRestriction> getAdditionalGradingLanguageRestriction();

    class Builder extends ImmutableProgrammingSubmissionData.Builder {}
}
