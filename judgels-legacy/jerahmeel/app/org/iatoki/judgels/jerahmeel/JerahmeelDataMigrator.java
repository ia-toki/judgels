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
        return 8;
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
        if (currentDataVersion < 7) {
            migrateV6toV7();
        }
        if (currentDataVersion < 8) {
            migrateV7toV8();
        }
    }

    private void migrateV7toV8() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        String[] tables = {
                "point_statistic",
                "problem_score_statistic",
                "problem_score_statistic_entry",
                "problem_statistic",
        };
        Statement statement = connection.createStatement();

        for (String table : tables) {
            statement.execute("ALTER TABLE jerahmeel_" + table + " ADD COLUMN time2 datetime(3);");
            statement.execute("UPDATE jerahmeel_" + table + " SET time2 = FROM_UNIXTIME(time * 0.001) WHERE time > 0;");
            statement.execute("ALTER TABLE jerahmeel_" + table + " DROP COLUMN time;");
            statement.execute("ALTER TABLE jerahmeel_" + table + " CHANGE COLUMN time2 time datetime(3);");
        }
    }

    private void migrateV6toV7() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        String[] tables = {
                "activity_log",
                "archive",
                "bundle_submission",
                "chapter",
                "chapter_dependency",
                "chapter_lesson",
                "chapter_problem",
                "container_problem_score_cache",
                "container_score_cache",
                "course",
                "course_chapter",
                "curriculum",
                "curriculum_course",
                "point_statistic",
                "point_statistic_entry",
                "problem_score_statistic",
                "problem_score_statistic_entry",
                "problem_set",
                "problem_set_problem",
                "problem_statistic",
                "problem_statistic_entry",
                "programming_grading",
                "programming_submission",
                "jid_cache",
                "user",
                "user_item",
        };

        Statement statement = connection.createStatement();

        for (String table : tables) {
            StringBuilder sb = new StringBuilder();
            sb.append("ALTER TABLE jerahmeel_").append(table)
                    .append(" ADD COLUMN createdAt DATETIME(3) NOT NULL DEFAULT NOW(3), ")
                    .append(" ADD COLUMN updatedAt DATETIME(3) NOT NULL DEFAULT NOW(3), ")
                    .append(" CHANGE COLUMN ipCreate createdIp VARCHAR(255), ")
                    .append(" CHANGE COLUMN ipUpdate updatedIp VARCHAR(255), ")
                    .append(" CHANGE COLUMN userCreate createdBy VARCHAR(255), ")
                    .append(" CHANGE COLUMN userUpdate updatedBy VARCHAR(255);");
            statement.execute(sb.toString());

            sb = new StringBuilder();
            sb.append("UPDATE jerahmeel_").append(table).append(" SET ")
                    .append("createdAt=FROM_UNIXTIME(timeCreate * 0.001), ")
                    .append("updatedAt=FROM_UNIXTIME(timeUpdate * 0.001);");
            statement.execute(sb.toString());

            sb = new StringBuilder();
            sb.append("ALTER TABLE jerahmeel_").append(table)
                    .append(" DROP COLUMN timeCreate, ")
                    .append(" DROP COLUMN timeUpdate;");
            statement.execute(sb.toString());

            sb = new StringBuilder();
            sb.append("ALTER TABLE jerahmeel_").append(table)
                    .append(" MODIFY COLUMN createdAt DATETIME(3) NOT NULL, ")
                    .append(" MODIFY COLUMN updatedAt DATETIME(3) NOT NULL; ");
            statement.execute(sb.toString());
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
