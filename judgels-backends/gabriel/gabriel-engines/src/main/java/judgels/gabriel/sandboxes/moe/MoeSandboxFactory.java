package judgels.gabriel.sandboxes.moe;

import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.SandboxInteractor;

public class MoeSandboxFactory implements SandboxFactory {
    private final String isolatePath;
    private final String iwrapperPath;

    public MoeSandboxFactory(String isolatePath, String iwrapperPath) {
        this.isolatePath = isolatePath;
        this.iwrapperPath = iwrapperPath;
    }

    @Override
    public Sandbox newSandbox() {
        return new IsolateSandbox(isolatePath, IsolateBoxIdFactory.newBoxId());
    }

    @Override
    public SandboxInteractor newSandboxInteractor() {
        return new IwrapperSandboxInteractor(iwrapperPath);
    }
}
