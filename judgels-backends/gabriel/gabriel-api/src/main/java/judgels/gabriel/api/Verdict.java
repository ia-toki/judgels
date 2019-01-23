package judgels.gabriel.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableVerdict.class)
public interface Verdict {
    String getCode();
    String getName();

    static Verdict of(String code, String name) {
        return ImmutableVerdict.builder().code(code).name(name).build();
    }
}
