package judgels.gabriel.api;

import java.util.List;
import java.util.Map;

public interface GradingConfig {
    Map<String, String> getSourceFileFields();
    int getTimeLimit();
    int getMemoryLimit();
    List<TestGroup> getTestData();
    List<Subtask> getSubtasks();
}
