package judgels.catalog.problem.programming.grading;

import java.util.List;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;
import judgels.grading.api.TestGroup;
import judgels.grading.engines.interactive.InteractiveWithSubtasksGradingConfig;

public class InteractiveWithSubtasksGradingConfigAutoPopulator extends BaseGradingConfigAutoPopulator {
    @Override
    @SuppressWarnings("unchecked")
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        Object[] parts = autoPopulateTestDataByTCFrameFormat(false, config.getSubtasks(), testDataFiles);

        return new InteractiveWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData((List<TestGroup>) parts[0])
                .subtaskPoints((List<Integer>) parts[1])
                .build();
    }
}
