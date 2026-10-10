package judgels.api.catalog.problem.editorial;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.CatalogFile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableProblemEditorialMediaFilesResponse.class)
public interface ProblemEditorialMediaFilesResponse {
    List<CatalogFile> getData();

    class Builder extends ImmutableProblemEditorialMediaFilesResponse.Builder {}
}
