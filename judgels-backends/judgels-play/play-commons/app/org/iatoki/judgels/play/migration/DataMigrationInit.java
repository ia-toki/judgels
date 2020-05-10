package org.iatoki.judgels.play.migration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

@Singleton
public final class DataMigrationInit {

    @Inject
    public DataMigrationInit(JudgelsDataMigrator dataMigrator) {
        try {
            dataMigrator.migrate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
