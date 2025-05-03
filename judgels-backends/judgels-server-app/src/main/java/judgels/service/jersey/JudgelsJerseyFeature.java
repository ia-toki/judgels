package judgels.service.jersey;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import judgels.service.actor.IpAddressFilter;

public enum JudgelsJerseyFeature implements Feature {
    INSTANCE;

    @Override
    public boolean configure(FeatureContext context) {
        context.register(IllegalArgumentExceptionMapper.class);
        context.register(JudgelsServiceExceptionMapper.class);
        context.register(EmptyOptionalExceptionMapper.class);
        context.register(IpAddressFilter.class);

        return true;
    }
}
