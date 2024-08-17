package judgels.gabriel.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GradingConfig {
    Map<String, String> getSourceFileFields();
    int getTimeLimit();
    int getMemoryLimit();
    List<TestGroup> getTestData();
    List<Subtask> getSubtasks();

    @JsonInclude(Include.NON_ABSENT)
    Optional<ScoringConfig> getScoringConfig();
}
