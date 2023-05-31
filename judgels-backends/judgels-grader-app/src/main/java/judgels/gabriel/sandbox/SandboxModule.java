package judgels.gabriel.sandbox;

import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.sandboxes.fake.FakeSandboxFactory;
import judgels.gabriel.sandboxes.isolate.IsolateSandboxFactory;

@Module
public class SandboxModule {
    private SandboxModule() {}

    @Provides
    static SandboxFactory sandboxFactory(Optional<IsolateSandboxFactory> isolateSandboxFactory) {
        if (isolateSandboxFactory.isPresent()) {
            return isolateSandboxFactory.get();
        } else {
            try {
                return new FakeSandboxFactory(Files.createTempDirectory("sandbox").toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
