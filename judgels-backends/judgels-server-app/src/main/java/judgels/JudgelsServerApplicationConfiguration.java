package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import judgels.jophiel.JophielConfiguration;
import judgels.training.TrainingConfiguration;

public class JudgelsServerApplicationConfiguration extends Configuration {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JudgelsServerConfiguration judgelsConfig;
    private final JophielConfiguration jophielConfig;
    private final TrainingConfiguration trainingConfig;

    public JudgelsServerApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("judgels") JudgelsServerConfiguration judgelsConfig,
            @JsonProperty("jophiel") JophielConfiguration jophielConfig,
            @JsonProperty("training") TrainingConfiguration trainingConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.judgelsConfig = judgelsConfig;
        this.jophielConfig = jophielConfig;
        this.trainingConfig = trainingConfig;
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

    public TrainingConfiguration getTrainingConfig() {
        return trainingConfig;
    }
}
