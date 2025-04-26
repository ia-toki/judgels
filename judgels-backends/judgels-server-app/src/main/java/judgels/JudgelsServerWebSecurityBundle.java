package judgels;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Environment;
import java.util.EnumSet;
import javax.servlet.DispatcherType;
import judgels.WebSecurityConfiguration.CorsConfiguration;
import org.eclipse.jetty.servlets.CrossOriginFilter;

public class JudgelsServerWebSecurityBundle implements ConfiguredBundle<JudgelsServerApplicationConfiguration> {
    @Override
    public void run(JudgelsServerApplicationConfiguration config, Environment env) throws Exception {
        CorsConfiguration corsConfig = config.getWebSecurityConfig().getCorsConfig();

        var cors = env.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "Accept,Authorization,Content-Type,Origin,X-Requested-With");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "DELETE,GET,HEAD,POST,PUT");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, corsConfig.getAllowedOrigins());
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, Boolean.toString(corsConfig.getAllowCredentials()));
    }
}
