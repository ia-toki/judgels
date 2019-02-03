package org.iatoki.judgels.sandalphon;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Singleton;
import com.typesafe.config.ConfigFactory;
import judgels.sandalphon.SandalphonConfiguration;
import org.apache.commons.io.FileUtils;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.iatoki.judgels.GitCommit;
import org.iatoki.judgels.GitProvider;
import org.iatoki.judgels.LocalFileSystemProvider;
import org.iatoki.judgels.LocalGitProvider;
import org.iatoki.judgels.play.migration.AbstractJudgelsDataMigrator;
import org.iatoki.judgels.play.migration.DataMigrationEntityManager;
import org.iatoki.judgels.play.migration.DataVersionDao;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

@Singleton
public final class SandalphonDataMigrator extends AbstractJudgelsDataMigrator {

    private EntityManager entityManager;

    @Inject
    public SandalphonDataMigrator(DataVersionDao dataVersionDao) {
        super(dataVersionDao);
        this.entityManager = DataMigrationEntityManager.createEntityManager();
    }

    @Override
    public long getLatestDataVersion() {
        return 5;
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
    }

    private void migrateV4toV5() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        String[] tables = {
                "activity_log",
                "bundle_grading",
                "bundle_submission",
                "client",
                "grader",
                "lesson",
                "lesson_partner",
                "problem",
                "problem_partner",
                "programming_grading",
                "programming_submission",
                "jid_cache",
                "user",
        };

        Statement statement = connection.createStatement();

        for (String table : tables) {
            StringBuilder sb = new StringBuilder();
            sb.append("ALTER TABLE sandalphon_").append(table)
                    .append(" ADD COLUMN createdAt DATETIME(3) NOT NULL DEFAULT NOW(3), ")
                    .append(" ADD COLUMN updatedAt DATETIME(3) NOT NULL DEFAULT NOW(3), ")
                    .append(" CHANGE COLUMN ipCreate createdIp VARCHAR(255), ")
                    .append(" CHANGE COLUMN ipUpdate updatedIp VARCHAR(255), ")
                    .append(" CHANGE COLUMN userCreate createdBy VARCHAR(255), ")
                    .append(" CHANGE COLUMN userUpdate updatedBy VARCHAR(255);");
            statement.execute(sb.toString());

            sb = new StringBuilder();
            sb.append("UPDATE sandalphon_").append(table).append(" SET ")
                    .append("createdAt=FROM_UNIXTIME(timeCreate * 0.001), ")
                    .append("updatedAt=FROM_UNIXTIME(timeUpdate * 0.001);");
            statement.execute(sb.toString());

            sb = new StringBuilder();
            sb.append("ALTER TABLE sandalphon_").append(table)
                    .append(" DROP COLUMN timeCreate, ")
                    .append(" DROP COLUMN timeUpdate;");
            statement.execute(sb.toString());

            sb = new StringBuilder();
            sb.append("ALTER TABLE sandalphon_").append(table)
                    .append(" MODIFY COLUMN createdAt DATETIME(3) NOT NULL, ")
                    .append(" MODIFY COLUMN updatedAt DATETIME(3) NOT NULL; ");
            statement.execute(sb.toString());
        }
    }

    private void migrateV3toV4() throws SQLException {
        SandalphonConfiguration config = SandalphonProperties.build(ConfigFactory.load());

        File[] baseDirs = new File[]{
                FileUtils.getFile(config.getBaseDataDir(), SandalphonProperties.getInstance().getBaseProblemsDirKey()),
                FileUtils.getFile(config.getBaseDataDir(), SandalphonProperties.getInstance().getBaseLessonsDirKey())
        };
        File[] clonesDirs = new File[]{
                FileUtils.getFile(config.getBaseDataDir(), SandalphonProperties.getInstance().getBaseProblemClonesDirKey()),
                FileUtils.getFile(config.getBaseDataDir(), SandalphonProperties.getInstance().getBaseLessonClonesDirKey())
        };
        String[] tableNames = new String[]{"problem", "lesson"};

        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        Statement statement = connection.createStatement();

        Map<String, String> jidCache = Maps.newHashMap();
        ResultSet cache = statement.executeQuery("SELECT * FROM sandalphon_jid_cache;");
        while (cache.next()) {
            String jid = cache.getString("jid");
            String displayName = cache.getString("displayName");
            jidCache.put(jid, displayName);
        }

        for (int i = 0; i < 2; i++) {
            String table = "sandalphon_" + tableNames[i];

            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table + ";");
            while (resultSet.next()) {
                String jid = resultSet.getString("jid");
                String name = resultSet.getString("name");
                String slug = "";
                for (char c : name.toCharArray()) {
                    if (Character.isLetter(c)) {
                        slug += c;
                    } else if (c == ' ') {
                        slug += '-';
                    }
                }

                slug = slug.toLowerCase();

                PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + table + " SET slug = ? WHERE jid = ?;");
                updateStatement.setString(1, slug);
                updateStatement.setString(2, jid);
                updateStatement.executeUpdate();

                File entityDir = FileUtils.getFile(baseDirs[i], jid);

                if (!entityDir.exists()) {
                    System.out.println("Directory not found: " + entityDir.getAbsolutePath());
                    continue;
                }

                File statementsDir = FileUtils.getFile(entityDir, "statements");
                File availableLanguagesFile = FileUtils.getFile(statementsDir, "availableLanguages.txt");

                String availableLanguagesString = "{}";
                try {
                    availableLanguagesString = FileUtils.readFileToString(availableLanguagesFile);
                } catch (IOException e) {
                    // ignore
                }

                Map<String, StatementLanguageStatus> availableLanguages = new Gson().fromJson(availableLanguagesString, new TypeToken<Map<String, StatementLanguageStatus>>() { }.getType());

                for (String languageCode : availableLanguages.keySet()) {
                    File statementDir = FileUtils.getFile(statementsDir, languageCode);

                    try {
                        FileUtils.forceMkdir(statementDir);

                        FileUtils.moveFile(FileUtils.getFile(statementsDir, languageCode + ".html"), FileUtils.getFile(statementDir, "text.html"));
                        FileUtils.writeStringToFile(FileUtils.getFile(statementDir, "title.txt"), name);
                    } catch (IOException e) {
                        // ignore
                    }
                }

                GitProvider git = new LocalGitProvider(new LocalFileSystemProvider(entityDir));

                StringBuilder sb = new StringBuilder();
                sb.append("Past commits:");
                sb.append("\n\n");

                for (GitCommit commit : git.getLog(ImmutableList.of())) {
                    sb.append("- ").append(jidCache.get(commit.getUserJid()));
                    sb.append(" (").append(commit.getTime()).append(")");
                    sb.append(": ").append(commit.getTitle());
                    sb.append("\n");
                }

                // reinitialize git

                try {
                    FileUtils.deleteDirectory(FileUtils.getFile(entityDir, ".git"));
                } catch (IOException e) {
                    // ignore
                }
                git.init(ImmutableList.of());
                git.addAll(ImmutableList.of());
                git.commit(ImmutableList.of(), "admin", "no@email.com", "[MIGRATION] Introduce slug system", sb.toString());
            }

            try {
                FileUtils.cleanDirectory(clonesDirs[i]);
            } catch (IOException e) {
                // ignore
            }
            statement.execute("ALTER TABLE " + table + " DROP name;");

        }
    }

    private void migrateV2toV3() throws SQLException {
        SessionImpl session = (SessionImpl) entityManager.unwrap(Session.class);
        Connection connection = session.getJdbcConnectionAccess().obtainConnection();

        String jidCacheTable = "sandalphon_jid_cache";
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

        String programmingSubmissionTable = "sandalphon_submission_programming";
        String newProgrammingSubmissionTable = "sandalphon_programming_submission";
        String bundleSubmissionTable = "sandalphon_submission_bundle";
        String newBundleSubmissionTable = "sandalphon_bundle_submission";
        Statement statement = connection.createStatement();

        statement.execute("ALTER TABLE " + programmingSubmissionTable + " CHANGE contestJid containerJid VARCHAR(255);");
        statement.execute("ALTER TABLE " + bundleSubmissionTable + " CHANGE contestJid containerJid VARCHAR(255);");

        statement.execute("DROP TABLE " + newProgrammingSubmissionTable + ";");
        statement.execute("DROP TABLE " + newBundleSubmissionTable + ";");

        statement.execute("RENAME TABLE " + programmingSubmissionTable + " TO " + newProgrammingSubmissionTable + ";");
        statement.execute("RENAME TABLE " + bundleSubmissionTable + " TO " + newBundleSubmissionTable + ";");
    }
}
