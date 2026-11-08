package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class JudgelsServerApplicationConfiguration extends Configuration {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JudgelsServerConfiguration judgelsConfig;

    public JudgelsServerApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("judgels") JudgelsServerConfiguration judgelsConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.judgelsConfig = judgelsConfig;
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
}
