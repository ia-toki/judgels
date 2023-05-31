package judgels.gabriel.sandboxes.isolate;

import java.nio.file.Path;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.SandboxInteractor;

public class IsolateSandboxFactory implements SandboxFactory {
    private final Path isolatePath;
    private final Path iwrapperPath;

    public IsolateSandboxFactory(Path baseIsolateDir) {
        this.isolatePath = baseIsolateDir.resolve("isolate");
        this.iwrapperPath = baseIsolateDir.resolve("iwrapper");
    }

    @Override
    public Sandbox newSandbox() {
        return new IsolateSandbox(isolatePath.toString(), IsolateBoxIdFactory.newBoxId());
    }

    @Override
    public SandboxInteractor newSandboxInteractor() {
        return new IwrapperSandboxInteractor(iwrapperPath.toString());
    }
}
