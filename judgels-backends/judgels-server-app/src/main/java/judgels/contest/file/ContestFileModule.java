package judgels.contest.file;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import java.util.Optional;
import judgels.core.JudgelsBaseDataDir;
import judgels.core.fs.FileSystem;
import judgels.core.fs.local.LocalFileSystem;

@Module
public class ContestFileModule {
    private final Optional<FileSystem> fs;

    public ContestFileModule() {
        this.fs = Optional.empty();
    }

    public ContestFileModule(FileSystem fs) {
        this.fs = Optional.of(fs);
    }

    @Provides
    @Singleton
    @ContestFileFs
    FileSystem fileFs(@JudgelsBaseDataDir Path baseDataDir) {
        return fs.orElse(new LocalFileSystem(baseDataDir.resolve("files")));
    }
}
