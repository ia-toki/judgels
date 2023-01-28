package judgels.michael;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;

public class MichaelMigrationsBundle extends MigrationsBundle<MichaelApplicationConfiguration> {
    @Override
    public PooledDataSourceFactory getDataSourceFactory(MichaelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
