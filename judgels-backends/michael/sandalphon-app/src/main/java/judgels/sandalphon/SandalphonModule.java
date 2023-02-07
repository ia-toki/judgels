package judgels.sandalphon;

import dagger.Module;
import dagger.Provides;
import java.nio.file.Paths;
import javax.inject.Singleton;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.sandalphon.lesson.LessonFs;
import judgels.sandalphon.lesson.LessonGit;
import judgels.sandalphon.problem.base.ProblemFs;
import judgels.sandalphon.problem.base.ProblemGit;
import judgels.sandalphon.problem.base.submission.SubmissionFs;

@Module
public class SandalphonModule {
    private final SandalphonConfiguration config;

    public SandalphonModule(SandalphonConfiguration config) {
        this.config = config;
    }

    @Provides
    SandalphonConfiguration sandalphonConfig() {
        return config;
    }

    @Provides
    @Singleton
    @ProblemFs
    static FileSystem problemFs(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir()));
    }

    @Provides
    @Singleton
    @SubmissionFs
    static FileSystem submissionFs(SandalphonConfiguration config) {
        return new LocalFileSystem(Paths.get(config.getBaseDataDir(), "submissions"));
    }

    @Provides
    @Singleton
    @LessonFs
    static FileSystem lessonFs(SandalphonConfiguration config) {
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
