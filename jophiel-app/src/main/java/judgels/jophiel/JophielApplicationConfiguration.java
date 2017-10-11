package judgels.jophiel;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class JophielApplicationConfiguration extends Configuration {
    private final DataSourceFactory databaseConfig;

    public JophielApplicationConfiguration(
            @JsonProperty("database") DataSourceFactory databaseConfig) {

        this.databaseConfig = databaseConfig;
    }

    public DataSourceFactory getDatabaseConfig() {
        return databaseConfig;
    }
}
