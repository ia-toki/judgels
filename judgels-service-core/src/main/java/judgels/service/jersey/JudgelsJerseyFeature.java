package judgels.service.jersey;

import com.palantir.remoting3.servers.jersey.HttpRemotingJerseyFeature;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import judgels.service.actor.IpAddressFilter;

public enum JudgelsJerseyFeature implements Feature {
    INSTANCE;

    @Override
    public boolean configure(FeatureContext context) {
        HttpRemotingJerseyFeature.INSTANCE.configure(context);

        context.register(new EmptyOptionalExceptionMapper());
        context.register(new NotAuthorizedExceptionMapper());

        context.register(new IpAddressFilter());

        return true;
    }
}
