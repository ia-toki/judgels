package org.iatoki.judgels.gabriel.blackbox;

import org.iatoki.judgels.gabriel.Sandbox;

public interface CompilationExecutor {
    CompilationVerdict compile(Sandbox sandbox, GradingContext context);
}
