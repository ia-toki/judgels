package judgels.michael;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.websecurity.WebSecurityConfigurable;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import judgels.jerahmeel.JerahmeelConfiguration;
import judgels.jophiel.JophielConfiguration;
import judgels.sandalphon.SandalphonConfiguration;
import judgels.uriel.UrielConfiguration;

public class MichaelApplicationConfiguration extends Configuration implements WebSecurityConfigurable {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JophielConfiguration jophielConfig;
    private final SandalphonConfiguration sandalphonConfig;
    private final UrielConfiguration urielConfig;
    private final JerahmeelConfiguration jerahmeelConfig;

    public MichaelApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("jophiel") JophielConfiguration jophielConfig,
            @JsonProperty("sandalphon") SandalphonConfiguration sandalphonConfig,
            @JsonProperty("uriel") UrielConfiguration urielConfig,
            @JsonProperty("jerahmeel") JerahmeelConfiguration jerahmeelConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.jophielConfig = jophielConfig;
        this.sandalphonConfig = sandalphonConfig;
        this.urielConfig = urielConfig;
        this.jerahmeelConfig = jerahmeelConfig;
    }

    public DataSourceFactory getDatabaseConfig() {
        return databaseConfig;
    }

    @Override
    public WebSecurityConfiguration getWebSecurityConfiguration() {
        return webSecurityConfig;
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
