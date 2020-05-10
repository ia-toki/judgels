package org.iatoki.judgels.play.migration;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractJudgelsDataMigrator implements JudgelsDataMigrator {

    private DataVersionDao dataVersionDao;
    private EntityManager entityManager;

    public AbstractJudgelsDataMigrator(DataVersionDao dataVersionDao) {
        this.dataVersionDao = dataVersionDao;
        this.entityManager = DataMigrationEntityManager.createEntityManager();
    }

    @Override
    public final void migrate() throws SQLException {
        checkTable();

        long currentDataVersion = dataVersionDao.getVersion();
        long latestDataVersion = getLatestDataVersion();
        if (currentDataVersion != latestDataVersion) {
            migrate(currentDataVersion);

            JPA.withTransaction(() -> dataVersionDao.update(latestDataVersion));
        }
    }

    protected abstract void migrate(long currentDataVersion) throws SQLException;

    private void checkTable() throws SQLException {
        String tableName = "judgels_data_version";

        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery("SHOW TABLES LIKE '" + tableName + "';");

        if (!resultSet.next()) {
            statement.execute("CREATE TABLE " + tableName + "("
                    + "id bigint(20) NOT NULL AUTO_INCREMENT,"
                    + "version bigint(20) NOT NULL,"
                    + "PRIMARY KEY (id)"
                    + ");");
            statement.executeUpdate("INSERT INTO `judgels_data_version` (`version`) VALUES (" + getLatestDataVersion() + ");");
        }

        resultSet.close();
        statement.close();
    }
}
