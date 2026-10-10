package judgels.catalog.problem.programming.grading;

import java.util.List;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;
import judgels.grading.engines.functional.FunctionalGradingConfig;

public class FunctionalGradingConfigAutoPopulator extends BaseGradingConfigAutoPopulator {
    @Override
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        return new FunctionalGradingConfig.Builder()
                .from(config)
                .testData(autoPopulateTestDataByFilename(true, testDataFiles))
                .build();
    }
}
