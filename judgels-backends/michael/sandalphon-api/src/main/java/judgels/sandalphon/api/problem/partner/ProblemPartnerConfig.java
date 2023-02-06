package judgels.sandalphon.api.problem.partner;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Set;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemPartnerConfig.class)
public interface ProblemPartnerConfig {
    boolean getIsAllowedToUpdateProblem();

    boolean getIsAllowedToUpdateStatement();
    boolean getIsAllowedToUploadStatementResources();
    Set<String> getAllowedStatementLanguagesToView();
    Set<String> getAllowedStatementLanguagesToUpdate();
    boolean getIsAllowedToManageStatementLanguages();

    boolean getIsAllowedToViewVersionHistory();
    boolean getIsAllowedToRestoreVersionHistory();

    class Builder extends ImmutableProblemPartnerConfig.Builder {}
}
