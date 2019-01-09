package judgels.gabriel.engines;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.Subtask;

public interface SingleSourceFileWithoutSubtasksGradingConfig extends SingleSourceFileGradingConfig {
    @Override
    default List<Subtask> getSubtasks() {
        return ImmutableList.of(Subtask.of(-1, 100));
    }
}
