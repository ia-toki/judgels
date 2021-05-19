package judgels.play.internal;

import java.io.File;
import java.util.Set;
import judgels.play.WebJarsExtractSpec;

public class DefaultWebJarsExtractSpec implements WebJarsExtractSpec {
    private final Set<File> classpath;
    private final File destinationDir;

    public DefaultWebJarsExtractSpec(Set<File> classpath, File destinationDir) {
        this.classpath = classpath;
        this.destinationDir = destinationDir;
    }

    @Override
    public Set<File> getClasspath() {
        return classpath;
    }

    @Override
    public File getDestinationDir() {
        return destinationDir;
    }
}
