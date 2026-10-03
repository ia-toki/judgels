package judgels.core.jersey;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import judgels.core.actor.PerRequestActorFilter;

public enum JudgelsJerseyFeature implements Feature {
    INSTANCE;

    @Override
    public boolean configure(FeatureContext context) {
        context.register(IllegalArgumentExceptionMapper.class);
        context.register(JudgelsApiExceptionMapper.class);
        context.register(EmptyOptionalExceptionMapper.class);
        context.register(PerRequestActorFilter.class);

        return true;
    }
}
