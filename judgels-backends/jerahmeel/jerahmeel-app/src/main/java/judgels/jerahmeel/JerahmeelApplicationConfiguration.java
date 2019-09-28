package judgels.jerahmeel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.palantir.websecurity.WebSecurityConfigurable;
import com.palantir.websecurity.WebSecurityConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class JerahmeelApplicationConfiguration extends Configuration implements WebSecurityConfigurable {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JerahmeelConfiguration jerahmeelConfig;

    public JerahmeelApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("jerahmeel") JerahmeelConfiguration jerahmeelConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.jerahmeelConfig = jerahmeelConfig;
    }

    public DataSourceFactory getDatabaseConfig() {
        return databaseConfig;
    }

    @Override
    public WebSecurityConfiguration getWebSecurityConfiguration() {
        return webSecurityConfig;
    }

    public JerahmeelConfiguration getJerahmeelConfig() {
        return jerahmeelConfig;
    }
}
