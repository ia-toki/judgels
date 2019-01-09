package judgels.gabriel.api;

import java.util.List;
import java.util.Map;

public interface GradingConfig {
    Map<String, String> getSourceFileFields();
    int getTimeLimitInMilliseconds();
    int getMemoryLimitInKilobytes();
    List<TestGroup> getTestData();
    List<Subtask> getSubtasks();
}
