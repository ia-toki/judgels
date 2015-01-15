package org.iatoki.judgels.gabriel;

import org.iatoki.judgels.gabriel.sandboxes.FakeSandbox;

public enum ExecutionStatus {
    OK,
    RUNTIME_ERROR,
    TIME_LIMIT_EXCEEDED,
    MEMORY_LIMIT_EXCEEDED,
    FORBIDDEN_ACCESS,
    INTERNAL_ERROR;

    public static final class MoeIsolateSandboxProvider implements SandboxProvider {

        @Override
        public Sandbox newSandbox() {
            return new FakeSandbox();
        }
    }
}
