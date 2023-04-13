package judgels.gabriel.sandboxes.fake;

import java.io.File;
import java.io.IOException;
import judgels.gabriel.api.Sandbox;
import judgels.gabriel.api.SandboxFactory;
import judgels.gabriel.api.SandboxInteractor;
import org.apache.commons.io.FileUtils;

public class FakeSandboxFactory implements SandboxFactory {

    private final File baseDir;
    private int sandboxesCount;

    public FakeSandboxFactory(File baseDir) {
        this.baseDir = baseDir;
        this.sandboxesCount = 0;
    }

    @Override
    public Sandbox newSandbox() {
        sandboxesCount++;
        File sandboxDir = new File(baseDir, "" + sandboxesCount);

        try {
            FileUtils.forceMkdir(sandboxDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new FakeSandbox(sandboxDir);
    }

    @Override
    public SandboxInteractor newSandboxInteractor() {
        return new FakeSandboxInteractor();
    }
}
