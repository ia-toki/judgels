package org.iatoki.judgels.sandalphon.problem.programming.grading;

import java.util.List;
import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;

public interface ConfigurableWithAutoPopulation {

    GradingConfig updateConfigWithAutoPopulation(GradingConfig config, List<FileInfo> testDataFiles);
}
