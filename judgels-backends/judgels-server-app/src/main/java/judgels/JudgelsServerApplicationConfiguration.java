package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jophiel.JophielConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.uriel.UrielConfiguration;

public class JudgelsServerApplicationConfiguration extends Configuration {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JudgelsServerConfiguration judgelsConfig;
    private final JophielConfiguration jophielConfig;
    private final SandalphonConfiguration sandalphonConfig;
    private final UrielConfiguration urielConfig;
    private final JerahmeelConfiguration jerahmeelConfig;

    public JudgelsServerApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("judgels") JudgelsServerConfiguration judgelsConfig,
            @JsonProperty("jophiel") JophielConfiguration jophielConfig,
            @JsonProperty("sandalphon") SandalphonConfiguration sandalphonConfig,
            @JsonProperty("uriel") UrielConfiguration urielConfig,
            @JsonProperty("jerahmeel") JerahmeelConfiguration jerahmeelConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.judgelsConfig = judgelsConfig;
        this.jophielConfig = jophielConfig;
        this.sandalphonConfig = sandalphonConfig;
        this.urielConfig = urielConfig;
        this.jerahmeelConfig = jerahmeelConfig;
    }

    public DataSourceFactory getDatabaseConfig() {
        return databaseConfig;
    }

    public WebSecurityConfiguration getWebSecurityConfig() {
        return webSecurityConfig;
    }

    public JudgelsServerConfiguration getJudgelsConfig() {
        return judgelsConfig;
    }

    public JophielConfiguration getJophielConfig() {
        return jophielConfig;
    }

    public SandalphonConfiguration getSandalphonConfig() {
        return sandalphonConfig;
    }

    public UrielConfiguration getUrielConfig() {
        return urielConfig;
    }

    public JerahmeelConfiguration getJerahmeelConfig() {
        return jerahmeelConfig;
    }
}
