package judgels.michael;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.websecurity.WebSecurityConfigurable;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import judgels.jophiel.JophielConfiguration;

public class MichaelApplicationConfiguration extends Configuration implements WebSecurityConfigurable {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JophielConfiguration jophielConfig;

    public MichaelApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("jophiel") JophielConfiguration jophielConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.jophielConfig = jophielConfig;
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
}
