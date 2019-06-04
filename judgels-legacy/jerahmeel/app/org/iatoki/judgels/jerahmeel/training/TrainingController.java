package org.iatoki.judgels.jerahmeel.training;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.archive.Archive;
import org.iatoki.judgels.jerahmeel.archive.ArchiveService;
import org.iatoki.judgels.jerahmeel.archive.ArchiveWithScore;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.curriculum.Curriculum;
import org.iatoki.judgels.jerahmeel.curriculum.CurriculumService;
import org.iatoki.judgels.jerahmeel.training.html.listTrainingsView;
import org.iatoki.judgels.jerahmeel.training.html.listTrainingsWithScoreView;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public final class TrainingController extends AbstractTrainingController {

    private final CurriculumService curriculumService;
    private final ArchiveService archiveService;

    @Inject
    public TrainingController(CurriculumService curriculumService, ArchiveService archiveService) {
        this.curriculumService = curriculumService;
        this.archiveService = archiveService;
    }

    @Authenticated(GuestView.class)
    @Transactional
    public Result index() {
        return listTrainings();
    }

    @Authenticated(GuestView.class)
    @Transactional
    public Result listTrainings() {
        List<Curriculum> curriculums = curriculumService.getAllCurriculums();

        HtmlTemplate template = getBaseHtmlTemplate();
        if (JerahmeelUtils.isGuest()) {
            List<Archive> archives = archiveService.getChildArchives("");
            template.setContent(listTrainingsView.render(curriculums, archives));
        } else {
            List<ArchiveWithScore> archives = archiveService.getChildArchivesWithScore("", IdentityUtils.getUserJid());
            template.setContent(listTrainingsWithScoreView.render(curriculums, archives));
        }

        template.setMainTitle(Messages.get("training.home"));
        template.setPageTitle("Home");

        return renderTemplate(template);
    }
}
