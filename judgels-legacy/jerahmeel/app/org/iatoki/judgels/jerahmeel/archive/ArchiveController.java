package org.iatoki.judgels.jerahmeel.archive;

import org.iatoki.judgels.jerahmeel.JerahmeelUtils;
import org.iatoki.judgels.jerahmeel.archive.html.createArchiveView;
import org.iatoki.judgels.jerahmeel.archive.html.editArchiveView;
import org.iatoki.judgels.jerahmeel.archive.html.listArchivesAndProblemSetsView;
import org.iatoki.judgels.jerahmeel.archive.html.listArchivesAndProblemSetsWithScoreView;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.GuestView;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSet;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetService;
import org.iatoki.judgels.jerahmeel.problemset.ProblemSetWithScore;
import org.iatoki.judgels.jerahmeel.training.AbstractTrainingController;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.api.mvc.Call;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public final class ArchiveController extends AbstractTrainingController {

    private static final long PAGE_SIZE = 20;

    private final ArchiveService archiveService;
    private final ProblemSetService problemSetService;

    @Inject
    public ArchiveController(ArchiveService archiveService, ProblemSetService problemSetService) {
        this.archiveService = archiveService;
        this.problemSetService = problemSetService;
    }

    @Authenticated(value = GuestView.class)
    @Transactional
    public Result viewArchives(long archiveId) throws ArchiveNotFoundException {
        return showListArchivesProblemSets(archiveId, 0, "name", "asc", "");
    }

    @Authenticated(value = GuestView.class)
    @Transactional
    public Result listArchivesProblemSets(long archiveId, long pageIndex, String orderBy, String orderDir, String filterString) throws ArchiveNotFoundException {
        return showListArchivesProblemSets(archiveId, pageIndex, orderBy, orderDir, filterString);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createArchive(long parentArchiveId) throws ArchiveNotFoundException {
        ArchiveUpsertForm archiveUpsertData = new ArchiveUpsertForm();
        if (parentArchiveId != 0) {
            Archive archive = archiveService.findArchiveById(parentArchiveId);
            archiveUpsertData.parentJid = archive.getJid();
        }
        Form<ArchiveUpsertForm> archiveUpsertForm = Form.form(ArchiveUpsertForm.class).fill(archiveUpsertData);

        return showCreateArchive(parentArchiveId, archiveUpsertForm);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    @RequireCSRFCheck
    public Result postCreateArchive() {
        Form<ArchiveUpsertForm> archiveUpsertForm = Form.form(ArchiveUpsertForm.class).bindFromRequest();

        if (formHasErrors(archiveUpsertForm)) {
            return showCreateArchive(0, archiveUpsertForm);
        }

        ArchiveUpsertForm archiveUpsertData = archiveUpsertForm.get();
        if (!archiveUpsertData.parentJid.isEmpty() && !archiveService.archiveExistsByJid(archiveUpsertData.parentJid)) {
            archiveUpsertForm.reject(Messages.get("error.archive.notExist"));

            return showCreateArchive(0, archiveUpsertForm);
        }

        long archiveId = archiveService.createArchive(archiveUpsertData.parentJid, archiveUpsertData.name, archiveUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.ArchiveController.viewArchives(archiveId));
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editArchive(long archiveId) throws ArchiveNotFoundException {
        Archive archive = archiveService.findArchiveById(archiveId);
        ArchiveUpsertForm archiveUpsertData = new ArchiveUpsertForm();
        if (archive.getParentArchive() != null) {
            archiveUpsertData.parentJid = archive.getParentArchive().getJid();
        }
        archiveUpsertData.name = archive.getName();
        archiveUpsertData.description = archive.getDescription();

        Form<ArchiveUpsertForm> archiveUpsertForm = Form.form(ArchiveUpsertForm.class).fill(archiveUpsertData);

        return showEditArchive(archiveUpsertForm, archive);
    }

    @Authenticated(value = {LoggedIn.class, HasRole.class})
    @Authorized(value = "admin")
    @Transactional
    @RequireCSRFCheck
    public Result postEditArchive(long archiveId) throws ArchiveNotFoundException {
        Archive archive = archiveService.findArchiveById(archiveId);
        Form<ArchiveUpsertForm> archiveUpsertForm = Form.form(ArchiveUpsertForm.class).bindFromRequest();

        if (formHasErrors(archiveUpsertForm)) {
            return showEditArchive(archiveUpsertForm, archive);
        }

        ArchiveUpsertForm archiveUpsertData = archiveUpsertForm.get();
        archiveService.updateArchive(archive.getJid(), archiveUpsertData.parentJid, archiveUpsertData.name, archiveUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        return redirect(routes.ArchiveController.viewArchives(archive.getId()));
    }

    private Result showListArchivesProblemSets(long archiveId, long pageIndex, String orderBy, String orderDir, String filterString) throws ArchiveNotFoundException {
        Archive archive = archiveService.findArchiveById(archiveId);
        Archive parentArchive = archive.getParentArchive();

        HtmlTemplate template = getBaseHtmlTemplate();
        if (!JerahmeelUtils.isGuest()) {
            List<ArchiveWithScore> childArchivesWithScore =  archiveService.getChildArchivesWithScore(archive.getJid(), IdentityUtils.getUserJid());
            Page<ProblemSetWithScore> pageOfProblemSetsWithScore = problemSetService.getPageOfProblemSetsWithScore(archive, IdentityUtils.getUserJid(), pageIndex, PAGE_SIZE, orderBy, orderDir, filterString);

            template.setContent(listArchivesAndProblemSetsWithScoreView.render(archive, childArchivesWithScore, pageOfProblemSetsWithScore, orderBy, orderDir, filterString));
        } else {
            List<Archive> childArchives = archiveService.getChildArchives(archive.getJid());
            Page<ProblemSet> pageOfProblemSets = problemSetService.getPageOfProblemSets(archive, pageIndex, PAGE_SIZE, orderBy, orderDir, filterString);

            template.setContent(listArchivesAndProblemSetsView.render(archive, childArchives, pageOfProblemSets, orderBy, orderDir, filterString));
        }

        if (!archive.getDescription().isEmpty()) {
            template.setDescription(archive.getDescription());
        }

        final String parentArchiveName;
        final Call backCall;
        if (parentArchive == null) {
            parentArchiveName = Messages.get("training.home");
            backCall = org.iatoki.judgels.jerahmeel.training.routes.TrainingController.index();
        } else {
            parentArchiveName = parentArchive.getName();
            backCall = routes.ArchiveController.viewArchives(parentArchive.getId());
        }

        if (JerahmeelUtils.hasRole("admin")) {
            template.addMainButton(Messages.get("commons.button.edit"), routes.ArchiveController.editArchive(archiveId));
            template.addMainButton(Messages.get("archive.create"), routes.ArchiveController.createArchive(archiveId));
            template.addMainButton(Messages.get("archive.problemSet.create"), org.iatoki.judgels.jerahmeel.problemset.routes.ProblemSetController.createProblemSet(archive.getId()));

            template.setMainTitle(Messages.get("archive.archive") + " " + archive.getName());
        } else {
            template.setMainTitle(archive.getName());
        }

        template.setMainBackButton(Messages.get("archive.backTo") + " " + parentArchiveName, backCall);

        Archive arch = archive;
        while (arch != null) {
            template.markBreadcrumbLocation(arch.getName(), routes.ArchiveController.viewArchives(arch.getId()));
            arch = arch.getParentArchive();
        }
        template.setPageTitle("Archives");

        return renderTemplate(template);
    }

    private Result showCreateArchive(long parentArchiveId, Form<ArchiveUpsertForm> archiveUpsertForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createArchiveView.render(archiveUpsertForm, archiveService.getAllArchives()));
        template.setMainTitle(Messages.get("archive.create"));
        template.markBreadcrumbLocation(Messages.get("archive.create"), routes.ArchiveController.createArchive(parentArchiveId));
        template.setPageTitle("Archive - Create");
        return renderTemplate(template);
    }

    private Result showEditArchive(Form<ArchiveUpsertForm> archiveUpsertForm, Archive archive) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editArchiveView.render(archiveUpsertForm, archive.getId(), archiveService.getAllArchives().stream().filter(f -> !f.containsJidInHierarchy(archive.getJid())).collect(Collectors.toList())));
        template.setMainTitle(Messages.get("archive.archive") + "#" + archive.getId() + ": " + archive.getName());
        template.addMainButton(Messages.get("archive.enter"), routes.ArchiveController.viewArchives(archive.getId()));

        template.markBreadcrumbLocation(Messages.get("archive.edit"), routes.ArchiveController.editArchive(archive.getId()));
        template.setPageTitle("Archive - Edit");
        return renderTemplate(template);
    }
}
