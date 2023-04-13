package judgels.gabriel.api;

import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
public interface ProcessExecutionResult {
    int getExitCode();
    List<String> getOutputLines();

    class Builder extends ImmutableProcessExecutionResult.Builder {}
}
