package judgels.gabriel.engines;

import com.google.common.collect.ImmutableList;
import java.util.List;
import judgels.gabriel.api.Subtask;

public interface MultipleSourceFilesWithSubtasksGradingConfig extends MultipleSourceFilesGradingConfig {
    List<Integer> getSubtaskPoints();

    @Override
    default List<Subtask> getSubtasks() {
        ImmutableList.Builder<Subtask> subtasks = ImmutableList.builder();
        for (int i = 0; i < getSubtaskPoints().size(); i++) {
            subtasks.add(Subtask.of(i + 1, getSubtaskPoints().get(i)));
        }
        return subtasks.build();
    }
}
