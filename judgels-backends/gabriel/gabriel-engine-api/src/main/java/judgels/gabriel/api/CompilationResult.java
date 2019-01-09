package judgels.gabriel.api;

import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
public interface CompilationResult {
    CompilationVerdict getVerdict();
    Map<String, String> getOutputs();

    class Builder extends ImmutableCompilationResult.Builder {}
}
