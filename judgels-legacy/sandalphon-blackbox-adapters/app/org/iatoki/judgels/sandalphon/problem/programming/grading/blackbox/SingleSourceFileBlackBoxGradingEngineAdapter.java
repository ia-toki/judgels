package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import org.iatoki.judgels.gabriel.blackbox.configs.SingleSourceFileBlackBoxGradingConfig;

import java.util.List;

public abstract class SingleSourceFileBlackBoxGradingEngineAdapter extends AbstractBoxGradingEngineAdapter {
    protected final void fillSingleSourceFileBlackBoxGradingConfigFormPartsFromConfig(SingleSourceFileBlackBoxGradingConfigForm form, SingleSourceFileBlackBoxGradingConfig config) {
        fillAbstractBlackBoxGradingFormPartsFromConfig(form, config);
    }

    protected final List<Object> createSingleSourceFileBlackBoxGradingConfigPartsFromForm(SingleSourceFileBlackBoxGradingConfigForm form) {
        return createAbstractBlackBoxGradingConfigPartsFromForm(form);
    }
}
