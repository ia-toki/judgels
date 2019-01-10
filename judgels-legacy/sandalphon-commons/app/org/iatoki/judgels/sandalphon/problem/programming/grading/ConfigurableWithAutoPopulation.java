package org.iatoki.judgels.sandalphon.problem.programming.grading;

import org.iatoki.judgels.FileInfo;
import org.iatoki.judgels.gabriel.GradingConfig;

import java.util.List;

public interface ConfigurableWithAutoPopulation {

    GradingConfig updateConfigWithAutoPopulation(GradingConfig config, List<FileInfo> testDataFiles);
}
