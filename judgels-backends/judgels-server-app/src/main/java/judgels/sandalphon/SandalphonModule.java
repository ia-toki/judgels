package judgels.sandalphon;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Paths;
import javax.inject.Singleton;
import judgels.JudgelsAppConfiguration;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.sandalphon.lesson.LessonFs;
import judgels.sandalphon.lesson.LessonGit;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.base.ProblemGit;

@Module
public class SandalphonModule {
    private final JudgelsAppConfiguration appConfig;
    private final SandalphonConfiguration config;

    public SandalphonModule(JudgelsAppConfiguration appConfig, SandalphonConfiguration config) {
        this.appConfig = appConfig;
        this.config = config;
    }

    @Provides
    JudgelsAppConfiguration appConfig() {
        return appConfig;
    }

    @Provides
    SandalphonConfiguration sandalphonConfig() {
        return config;
    }

    @Provides
    @Singleton
    @ProblemFs
    FileSystem problemFs() {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    @Provides
    @Singleton
    @LessonFs
    FileSystem lessonFs() {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    @Provides
    @Singleton
    @ProblemGit
    static Git problemGit(@ProblemFs FileSystem fs) {
        return new LocalGit((LocalFileSystem) fs);
    }

    @Provides
    @Singleton
    @LessonGit
    static Git lessonGit(@LessonFs FileSystem fs) {
        return new LocalGit((LocalFileSystem) fs);
    }
}
