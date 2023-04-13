package judgels.michael.problem.programming.grading.config;

import java.util.List;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;

public interface GradingConfigAdapter {
    GradingConfigForm buildFormFromConfig(GradingConfig config);
    GradingConfig buildConfigFromForm(GradingConfigForm form);
    GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles);
}
