package org.iatoki.judgels.sandalphon;

import org.iatoki.judgels.play.AbstractJudgelsGlobal;
import play.Application;

public final class SandalphonGlobal extends AbstractJudgelsGlobal {

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        application.injector().instanceOf(SandalphonThreadsScheduler.class);
    }
}
