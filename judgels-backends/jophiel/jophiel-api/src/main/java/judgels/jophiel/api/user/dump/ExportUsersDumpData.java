package judgels.jophiel.api.user.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableExportUsersDumpData.class)
public interface ExportUsersDumpData {
    List<String> getUsernames();

    class Builder extends ImmutableExportUsersDumpData.Builder {}
}
