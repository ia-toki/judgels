package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableVerdict.class)
public abstract class Verdict {
    public abstract String getCode();
    public abstract String getName();

    public static Verdict of(String code, String name) {
        return ImmutableVerdict.builder().code(code).name(name).build();
    }
}
