package judgels.uriel.file;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Optional;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.service.JudgelsBaseDataDir;

@Module
public class FileModule {
    private final Optional<FileSystem> fs;

    public FileModule() {
        this.fs = Optional.empty();
    }

    public FileModule(FileSystem fs) {
        this.fs = Optional.of(fs);
    }

    @Provides
    @Singleton
    @FileFs
    FileSystem fileFs(@JudgelsBaseDataDir Path baseDataDir) {
        return fs.orElse(new LocalFileSystem(baseDataDir.resolve("files")));
    }
}
