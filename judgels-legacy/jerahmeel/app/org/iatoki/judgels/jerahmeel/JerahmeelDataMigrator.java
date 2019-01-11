package org.iatoki.judgels.jerahmeel;

import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.iatoki.judgels.play.migration.AbstractJudgelsDataMigrator;
import org.iatoki.judgels.play.migration.DataMigrationEntityManager;
import org.iatoki.judgels.play.migration.DataVersionDao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class JerahmeelDataMigrator extends AbstractJudgelsDataMigrator {

    private final EntityManager entityManager;

    @Inject
    public JerahmeelDataMigrator(DataVersionDao dataVersionDao) {
        super(dataVersionDao);
        this.entityManager = DataMigrationEntityManager.createEntityManager();
    }

    @Override
    public long getLatestDataVersion() {
        return 6;
    }

    @Override
    protected void migrate(long currentDataVersion) throws SQLException {
        if (currentDataVersion < 2) {
            migrateV1toV2();
        }
        if (currentDataVersion < 3) {
            migrateV2toV3();
        }
        if (currentDataVersion < 4) {
            migrateV3toV4();
        }
        if (currentDataVersion < 5) {
            migrateV4toV5();
        }
        if (currentDataVersion < 6) {
            migrateV5toV6();
        }
    }

    private void migrateV5toV6() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        Statement statement = connection.createStatement();

        statement.execute("ALTER TABLE jerahmeel_course_session CHANGE sessionJid chapterJid VARCHAR(255);");
        statement.execute("RENAME TABLE jerahmeel_course_session TO jerahmeel_course_chapter;");

        statement.execute("RENAME TABLE jerahmeel_session TO jerahmeel_chapter;");

        statement.execute("ALTER TABLE jerahmeel_session_dependency CHANGE dependedSessionJid dependedChapterJid VARCHAR(255);");
        statement.execute("ALTER TABLE jerahmeel_session_dependency CHANGE sessionJid chapterJid VARCHAR(255);");
        statement.execute("RENAME TABLE jerahmeel_session_dependency TO jerahmeel_chapter_dependency;");

        statement.execute("ALTER TABLE jerahmeel_session_lesson CHANGE sessionJid chapterJid VARCHAR(255);");
        statement.execute("RENAME TABLE jerahmeel_session_lesson TO jerahmeel_chapter_lesson;");

        statement.execute("ALTER TABLE jerahmeel_session_problem CHANGE sessionJid chapterJid VARCHAR(255);");
        statement.execute("RENAME TABLE jerahmeel_session_problem TO jerahmeel_chapter_problem;");


    }

    private void migrateV4toV5() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        Statement statement = connection.createStatement();

        statement.execute("TRUNCATE TABLE jerahmeel_container_problem_score_cache;");
        statement.execute("TRUNCATE TABLE jerahmeel_container_score_cache;");
    }

    private void migrateV3toV4() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        Statement statement = connection.createStatement();

        statement.execute("DROP TABLE jerahmeel_container_problem_score_cache;");
        statement.execute("RENAME TABLE jerahmeel_container_score_cache TO jerahmeel_container_problem_score_cache;");
    }

    private void migrateV2toV3() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        String jidCacheTable = "jerahmeel_jid_cache";
        Statement statement = connection.createStatement();
        String jidCacheQuery = "SELECT * FROM " + jidCacheTable + ";";
        ResultSet resultSet = statement.executeQuery(jidCacheQuery);

        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String jid = resultSet.getString("jid");
            String displayName = resultSet.getString("displayName");

            if (jid.startsWith("JIDUSER")) {
                if (displayName.contains("(")) {
                    displayName = displayName.substring(0, displayName.indexOf("(") - 1);

                    PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + jidCacheTable + " SET displayName= ? WHERE id=" + id + ";");
                    preparedStatement.setString(1, displayName);
                    preparedStatement.executeUpdate();
                }
            }
        }
    }

    private void migrateV1toV2() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        String programmingSubmissionTable = "jerahmeel_programming_submission";
        String bundleSubmissionTable = "jerahmeel_bundle_submission";
        Statement statement = connection.createStatement();

        statement.execute("ALTER TABLE " + bundleSubmissionTable + " DROP containerJid;");
        statement.execute("ALTER TABLE " + bundleSubmissionTable + " CHANGE contestJid containerJid VARCHAR(255);");
        statement.execute("ALTER TABLE " + programmingSubmissionTable + " DROP containerJid;");
        statement.execute("ALTER TABLE " + programmingSubmissionTable + " CHANGE contestJid containerJid VARCHAR(255);");
    }
}
