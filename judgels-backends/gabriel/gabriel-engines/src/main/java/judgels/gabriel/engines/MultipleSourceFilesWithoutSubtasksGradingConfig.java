package judgels.gabriel.engines;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.Subtask;

public interface MultipleSourceFilesWithoutSubtasksGradingConfig extends MultipleSourceFilesGradingConfig {
    @JsonIgnore
    @Override
    default List<Subtask> getSubtasks() {
        return ImmutableList.of(Subtask.of(-1, 100));
    }
}
