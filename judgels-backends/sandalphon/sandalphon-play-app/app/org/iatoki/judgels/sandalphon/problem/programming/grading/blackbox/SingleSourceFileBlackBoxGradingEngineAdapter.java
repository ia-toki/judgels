package org.iatoki.judgels.sandalphon.problem.programming.grading.blackbox;

import java.util.List;
import judgels.gabriel.engines.SingleSourceFileGradingConfig;

public abstract class SingleSourceFileBlackBoxGradingEngineAdapter extends AbstractBoxGradingEngineAdapter {
    protected final void fillSingleSourceFileBlackBoxGradingConfigFormPartsFromConfig(SingleSourceFileBlackBoxGradingConfigForm form, SingleSourceFileGradingConfig config) {
        fillAbstractBlackBoxGradingFormPartsFromConfig(form, config);
    }

    protected final List<Object> createSingleSourceFileBlackBoxGradingConfigPartsFromForm(SingleSourceFileBlackBoxGradingConfigForm form) {
        return createAbstractBlackBoxGradingConfigPartsFromForm(form);
    }
}
