package judgels.catalog;

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Singleton;
import java.nio.file.Path;
import judgels.catalog.lesson.LessonFs;
import judgels.catalog.lesson.LessonGit;
import judgels.catalog.problem.ProblemFs;
import judgels.catalog.problem.ProblemGit;
import judgels.core.JudgelsBaseDataDir;
import judgels.core.fs.FileSystem;
import judgels.core.fs.local.LocalFileSystem;
import judgels.core.git.Git;
import judgels.core.git.LocalGit;

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
