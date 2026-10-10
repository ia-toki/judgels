package judgels.catalog.problem.programming.grading;

import java.util.List;
import judgels.core.fs.FileInfo;
import judgels.grading.api.GradingConfig;

/** Fills a grading config's test data from the names of the problem's test data files. */
public interface GradingConfigAutoPopulator {
    GradingConfig autoPopulateTestData(GradingConfig config, List<FileInfo> testDataFiles);
}
