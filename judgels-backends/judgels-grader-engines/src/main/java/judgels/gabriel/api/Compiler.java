package judgels.gabriel.api;

import java.io.File;
import java.util.Map;

public interface Compiler {
    CompilationResult compile(Map<String, File> sourceFiles) throws CompilationException;
}
