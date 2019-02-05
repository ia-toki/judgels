package judgels.jerahmeel;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;

public class JerahmeelMigrationsBundle extends MigrationsBundle<JerahmeelApplicationConfiguration> {
    @Override
    public PooledDataSourceFactory getDataSourceFactory(JerahmeelApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
