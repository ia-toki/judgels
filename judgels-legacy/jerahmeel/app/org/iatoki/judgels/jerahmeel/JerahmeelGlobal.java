package org.iatoki.judgels.jerahmeel;

import org.iatoki.judgels.play.AbstractJudgelsGlobal;
import play.Application;

public final class JerahmeelGlobal extends AbstractJudgelsGlobal {

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        application.injector().instanceOf(JerahmeelThreadsScheduler.class);
    }
}
