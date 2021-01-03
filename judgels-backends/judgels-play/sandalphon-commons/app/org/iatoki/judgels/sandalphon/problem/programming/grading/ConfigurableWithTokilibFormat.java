package org.iatoki.judgels.sandalphon.problem.programming.grading;

import judgels.fs.FileInfo;
import judgels.gabriel.api.GradingConfig;

import java.util.List;

public interface ConfigurableWithTokilibFormat {

    GradingConfig updateConfigWithTokilibFormat(GradingConfig config, List<FileInfo> testDataFiles);
}
