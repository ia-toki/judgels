package judgels;

import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.migrations.MigrationsBundle;

public class JudgelsServerMigrationsBundle extends MigrationsBundle<JudgelsServerApplicationConfiguration> {
    @Override
    public PooledDataSourceFactory getDataSourceFactory(JudgelsServerApplicationConfiguration config) {
        return config.getDatabaseConfig();
    }
}
