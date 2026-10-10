package judgels.catalog.problem.programming.grading;

import java.util.List;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;
import judgels.grading.engines.interactive.InteractiveGradingConfig;

public class InteractiveGradingConfigAutoPopulator extends BaseGradingConfigAutoPopulator {
    @Override
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        return new InteractiveGradingConfig.Builder()
                .from(config)
                .testData(autoPopulateTestDataByFilename(false, testDataFiles))
                .build();
    }
}
