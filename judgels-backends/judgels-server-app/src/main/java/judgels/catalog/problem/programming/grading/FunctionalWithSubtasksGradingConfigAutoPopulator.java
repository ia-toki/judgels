package judgels.catalog.problem.programming.grading;

import java.util.List;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;
import judgels.grading.api.TestGroup;
import judgels.grading.engines.functional.FunctionalWithSubtasksGradingConfig;

public class FunctionalWithSubtasksGradingConfigAutoPopulator extends BaseGradingConfigAutoPopulator {
    @Override
    @SuppressWarnings("unchecked")
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        Object[] parts = autoPopulateTestDataByTCFrameFormat(true, config.getSubtasks(), testDataFiles);

        return new FunctionalWithSubtasksGradingConfig.Builder()
                .from(config)
                .testData((List<TestGroup>) parts[0])
                .subtaskPoints((List<Integer>) parts[1])
                .build();
    }
}
