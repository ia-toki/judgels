package judgels.core;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

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
