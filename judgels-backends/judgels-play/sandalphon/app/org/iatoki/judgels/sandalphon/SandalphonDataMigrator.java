package org.iatoki.judgels.sandalphon;

import com.google.inject.Singleton;
import java.sql.SQLException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.iatoki.judgels.play.migration.AbstractJudgelsDataMigrator;
import org.iatoki.judgels.play.migration.DataMigrationEntityManager;
import org.iatoki.judgels.play.migration.DataVersionDao;
import play.db.jpa.JPAApi;

@Singleton
public final class SandalphonDataMigrator extends AbstractJudgelsDataMigrator {

    private EntityManager entityManager;

    @Inject
    public SandalphonDataMigrator(JPAApi jpaApi, DataVersionDao dataVersionDao) {
        super(jpaApi, dataVersionDao);
        this.entityManager = DataMigrationEntityManager.createEntityManager();
    }

    @Override
    public long getLatestDataVersion() {
        return 5;
    }

    @Override
    protected void migrate(long currentDataVersion) throws SQLException {

    }
}
