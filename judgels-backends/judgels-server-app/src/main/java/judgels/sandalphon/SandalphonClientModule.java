package judgels.sandalphon;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import javax.inject.Singleton;
import judgels.JudgelsBaseDataDir;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.sandalphon.lesson.LessonFs;
import judgels.sandalphon.lesson.LessonGit;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.base.ProblemGit;

@Module
public class SandalphonClientModule {
    private SandalphonClientModule() {}

    @Provides
    @Singleton
    @ProblemFs
    static FileSystem problemFs(@JudgelsBaseDataDir Path baseDataDir) {
        return new LocalFileSystem(baseDataDir);
    }

    @Provides
    @Singleton
    @LessonFs
    static FileSystem lessonFs(@JudgelsBaseDataDir Path baseDataDir) {
        return new LocalFileSystem(baseDataDir);
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
