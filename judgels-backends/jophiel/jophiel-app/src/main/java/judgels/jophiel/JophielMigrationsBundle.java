package judgels.jophiel;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;

public class JophielMigrationsBundle extends MigrationsBundle<JophielApplicationConfiguration> {
    @Override
    public PooledDataSourceFactory getDataSourceFactory(JophielApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
