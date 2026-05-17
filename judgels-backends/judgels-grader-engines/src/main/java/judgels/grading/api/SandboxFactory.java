package judgels.grading.api;

public interface SandboxFactory {
    Sandbox newSandbox();
    SandboxInteractor newSandboxInteractor();
}
