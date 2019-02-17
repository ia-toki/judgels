package judgels.uriel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palantir.conjure.java.api.config.service.UserAgent;
import dagger.Module;
import dagger.Provides;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Singleton;
import judgels.jophiel.api.user.me.MyUserService;
import judgels.sandalphon.submission.programming.BaseSubmissionStore;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.JudgelsVersion;
import judgels.service.actor.ActorChecker;
import judgels.service.actor.CachingActorExtractor;
import judgels.uriel.persistence.ContestProgrammingGradingDao;
import judgels.uriel.persistence.ContestProgrammingSubmissionDao;

@Module
public class UrielModule {
    private final UrielConfiguration config;

    public UrielModule(UrielConfiguration config) {
        this.config = config;
    }

    @Provides
    @UrielBaseDataDir
    Path urielBaseDataDir() {
        return Paths.get(config.getBaseDataDir());
    }

    @Provides
    @Singleton
    static UserAgent userAgent() {
        return UserAgent.of(UserAgent.Agent.of("uriel", JudgelsVersion.INSTANCE));
    }

    @Provides
    @Singleton
    static ActorChecker actorChecker(MyUserService myUserService) {
        return new ActorChecker(new CachingActorExtractor(myUserService));
    }

    @Provides
    @Singleton
    static SubmissionStore submissionStore(
            ContestProgrammingSubmissionDao submissionDao,
            ContestProgrammingGradingDao gradingDao,
            ObjectMapper mapper) {

        return new BaseSubmissionStore<>(submissionDao, gradingDao, mapper);
    }
}
