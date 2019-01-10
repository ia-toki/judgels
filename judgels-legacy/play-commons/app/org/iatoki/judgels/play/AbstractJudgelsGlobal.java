package org.iatoki.judgels.play;

import org.iatoki.judgels.play.migration.DataMigrationInit;
import play.Application;
import play.GlobalSettings;

public abstract class AbstractJudgelsGlobal extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        application.injector().instanceOf(DataMigrationInit.class);
    }
}
