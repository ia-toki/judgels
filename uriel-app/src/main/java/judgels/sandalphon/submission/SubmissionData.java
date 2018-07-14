package judgels.sandalphon.submission;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Optional;
import judgels.gabriel.api.LanguageRestriction;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSubmissionData.class)
public interface SubmissionData {
    String getUserJid();
    String getProblemJid();
    String getContainerJid();
    String getGradingLanguage();
    Optional<LanguageRestriction> getAdditionalGradingLanguageRestriction();

    class Builder extends ImmutableSubmissionData.Builder {}
}
