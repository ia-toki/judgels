package judgels;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import java.util.Optional;
import tlx.training.TrainingConfiguration;

public class JudgelsServerApplicationConfiguration extends Configuration {
    private final DataSourceFactory databaseConfig;
    private final WebSecurityConfiguration webSecurityConfig;
    private final JudgelsServerConfiguration judgelsConfig;
    private final Optional<TrainingConfiguration> trainingConfig;

    public JudgelsServerApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig,
            @JsonProperty("webSecurity") WebSecurityConfiguration webSecurityConfig,
            @JsonProperty("judgels") JudgelsServerConfiguration judgelsConfig,
            @JsonProperty("training") Optional<TrainingConfiguration> trainingConfig) {

        this.databaseConfig = databaseConfig;
        this.webSecurityConfig = webSecurityConfig;
        this.judgelsConfig = judgelsConfig;
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

    public Optional<TrainingConfiguration> getTrainingConfig() {
        return trainingConfig;
    }
}
