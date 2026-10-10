package judgels.api.catalog.lesson.statement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import judgels.api.catalog.CatalogFile;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableLessonStatementMediaFilesResponse.class)
public interface LessonStatementMediaFilesResponse {
    List<CatalogFile> getData();

    class Builder extends ImmutableLessonStatementMediaFilesResponse.Builder {}
}
