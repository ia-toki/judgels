package judgels.catalog.problem.programming.grading;

import java.util.List;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;
import judgels.grading.engines.batch.BatchGradingConfig;

public class BatchGradingConfigAutoPopulator extends BaseGradingConfigAutoPopulator {
    @Override
    public GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles) {
        return new BatchGradingConfig.Builder()
                .from(config)
                .testData(autoPopulateTestDataByFilename(true, testDataFiles))
                .build();
    }
}
