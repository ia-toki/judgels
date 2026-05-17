package judgels.grading.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonDeserialize(as = ImmutableSourceFile.class)
public interface SourceFile {
    String getName();
    byte[] getContent();

    class Builder extends ImmutableSourceFile.Builder {}
}
