package judgels.jerahmeel.chapter.submission;

import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import judgels.jerahmeel.api.chapter.Chapter;
import judgels.jerahmeel.api.chapter.submission.programming.ChapterSubmissionService;
import judgels.jerahmeel.api.chapter.submission.programming.ChapterSubmissionsResponse;
import judgels.jerahmeel.chapter.ChapterStore;
import judgels.jerahmeel.chapter.problem.ChapterProblemStore;
import judgels.jophiel.api.profile.Profile;
import judgels.jophiel.api.profile.ProfileService;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.submission.programming.Submission;
import judgels.sandalphon.submission.programming.SubmissionStore;
import judgels.service.actor.ActorChecker;
import judgels.service.api.actor.AuthHeader;

public class ChapterSubmissionResource implements ChapterSubmissionService {
    private final ActorChecker actorChecker;
    private final ChapterStore chapterStore;
    private final SubmissionStore submissionStore;
    private final ChapterProblemStore problemStore;
    private final ProfileService profileService;

    @Inject
    public ChapterSubmissionResource(
            ActorChecker actorChecker,
            ChapterStore chapterStore,
            SubmissionStore submissionStore,
            ChapterProblemStore problemStore,
            ProfileService profileService) {

        this.actorChecker = actorChecker;
        this.chapterStore = chapterStore;
        this.submissionStore = submissionStore;
        this.problemStore = problemStore;
        this.profileService = profileService;
    }

    @Override
    @UnitOfWork(readOnly = true)
    public ChapterSubmissionsResponse getSubmissions(
            Optional<AuthHeader> authHeader,
            String chapterJid,
            Optional<String> userJid,
            Optional<String> problemJid,
            Optional<Integer> page) {

        String actorJid = actorChecker.check(authHeader);
        Chapter chapter = checkFound(chapterStore.getChapterByJid(chapterJid));

        Page<Submission> submissions = submissionStore.getSubmissions(chapter.getJid(), userJid, problemJid, page);

        Set<String> problemJids = submissions.getPage().stream()
                .map(Submission::getProblemJid)
                .collect(Collectors.toSet());

        Set<String> userJids = submissions.getPage().stream().map(Submission::getUserJid).collect(Collectors.toSet());
        Map<String, Profile> profilesMap = userJids.isEmpty()
                ? Collections.emptyMap()
                : profileService.getProfiles(userJids);

        Map<String, String> problemAliasesMap = problemStore.getProblemAliasesByJids(chapter.getJid(), problemJids);

        return new ChapterSubmissionsResponse.Builder()
                .data(submissions)
                .profilesMap(profilesMap)
                .problemAliasesMap(problemAliasesMap)
                .build();
    }
}
