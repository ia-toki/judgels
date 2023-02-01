package judgels.michael;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.websecurity.WebSecurityConfigurable;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import judgels.jophiel.JophielConfiguration;
import judgels.uriel.UrielConfiguration;

public class MichaelApplicationConfiguration extends Configuration implements WebSecurityConfigurable {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JophielConfiguration jophielConfig;
    private final UrielConfiguration urielConfig;

    public MichaelApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("jophiel") JophielConfiguration jophielConfig,
            @JsonProperty("uriel") UrielConfiguration urielConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.jophielConfig = jophielConfig;
        this.urielConfig = urielConfig;
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

    public UrielConfiguration getUrielConfig() {
        return urielConfig;
    }
}
