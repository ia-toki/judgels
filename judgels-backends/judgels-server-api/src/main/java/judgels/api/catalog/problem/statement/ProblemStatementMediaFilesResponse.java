package judgels.api.catalog.problem.statement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.CatalogFile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemStatementMediaFilesResponse.class)
public interface ProblemStatementMediaFilesResponse {
    List<CatalogFile> getData();

    class Builder extends ImmutableProblemStatementMediaFilesResponse.Builder {}
}
