package judgels.grading.sandboxes.isolate;

import java.nio.file.Path;
import judgels.grading.api.Sandbox;
import judgels.grading.api.SandboxFactory;
import judgels.grading.api.SandboxInteractor;

public class IsolateSandboxFactory implements SandboxFactory {
    private final Path isolatePath;
    private final Path iwrapperPath;

    public IsolateSandboxFactory(Path baseIsolateDir) {
        this.isolatePath = baseIsolateDir.resolve("bin").resolve("isolate");
        this.iwrapperPath = baseIsolateDir.resolve("bin").resolve("iwrapper");
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
