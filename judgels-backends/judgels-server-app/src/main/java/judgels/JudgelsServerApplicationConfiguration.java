package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jophiel.JophielConfiguration;

public class JudgelsServerApplicationConfiguration extends Configuration {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JudgelsServerConfiguration judgelsConfig;
    private final JophielConfiguration jophielConfig;
    private final JerahmeelConfiguration jerahmeelConfig;

    public JudgelsServerApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("judgels") JudgelsServerConfiguration judgelsConfig,
            @JsonProperty("jophiel") JophielConfiguration jophielConfig,
            @JsonProperty("jerahmeel") JerahmeelConfiguration jerahmeelConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.judgelsConfig = judgelsConfig;
        this.jophielConfig = jophielConfig;
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

    public JerahmeelConfiguration getJerahmeelConfig() {
        return jerahmeelConfig;
    }
}
