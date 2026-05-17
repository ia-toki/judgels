package judgels.grading.engines;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.grading.api.Subtask;

public interface SingleSourceFileWithoutSubtasksGradingConfig extends SingleSourceFileGradingConfig {
    @JsonIgnore
    @Override
    default List<Subtask> getSubtasks() {
        return ImmutableList.of(Subtask.of(-1, 100));
    }
}
