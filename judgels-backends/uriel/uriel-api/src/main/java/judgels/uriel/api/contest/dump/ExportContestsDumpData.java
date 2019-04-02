package judgels.uriel.api.contest.dump;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.Map;
import java.util.Set;
import judgels.persistence.api.dump.DumpImportMode;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableExportContestsDumpData.class)
public interface ExportContestsDumpData {
    Map<String, ContestDumpEntry> getContests();

    @Value.Immutable
    @JsonDeserialize(as = ImmutableContestDumpEntry.class)
    interface ContestDumpEntry {
        DumpImportMode getMode();
        Set<ContestDumpComponent> getComponents();

        class Builder extends ImmutableContestDumpEntry.Builder {}
    }

    class Builder extends ImmutableExportContestsDumpData.Builder {}
}
