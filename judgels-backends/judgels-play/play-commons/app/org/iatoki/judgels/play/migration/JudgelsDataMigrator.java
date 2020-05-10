package org.iatoki.judgels.play.migration;

import java.sql.SQLException;

public interface JudgelsDataMigrator {

    long getLatestDataVersion();

    void migrate() throws SQLException;
}
