package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import judgels.gabriel.engines.SingleSourceFileGradingConfig;

import java.util.List;

public abstract class SingleSourceFileBlackBoxGradingEngineAdapter extends AbstractBoxGradingEngineAdapter {
    protected final void fillSingleSourceFileBlackBoxGradingConfigFormPartsFromConfig(SingleSourceFileBlackBoxGradingConfigForm form, SingleSourceFileGradingConfig config) {
        fillAbstractBlackBoxGradingFormPartsFromConfig(form, config);
    }

    protected final List<Object> createSingleSourceFileBlackBoxGradingConfigPartsFromForm(SingleSourceFileBlackBoxGradingConfigForm form) {
        return createAbstractBlackBoxGradingConfigPartsFromForm(form);
    }
}
