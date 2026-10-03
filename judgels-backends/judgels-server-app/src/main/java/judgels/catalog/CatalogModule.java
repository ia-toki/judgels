package judgels.catalog;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import judgels.fs.FileSystem;
import judgels.fs.local.LocalFileSystem;
import judgels.git.Git;
import judgels.git.LocalGit;
import judgels.lesson.LessonFs;
import judgels.lesson.LessonGit;
import judgels.problem.ProblemFs;
import judgels.problem.ProblemGit;
import judgels.service.JudgelsBaseDataDir;

@Module
public class CatalogModule {
    private CatalogModule() {}

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
