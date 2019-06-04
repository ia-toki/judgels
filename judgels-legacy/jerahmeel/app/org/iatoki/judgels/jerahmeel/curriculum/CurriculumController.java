package org.iatoki.judgels.jerahmeel.curriculum;

import org.iatoki.judgels.jerahmeel.JerahmeelControllerUtils;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authenticated;
import org.iatoki.judgels.jerahmeel.controllers.securities.Authorized;
import org.iatoki.judgels.jerahmeel.controllers.securities.HasRole;
import org.iatoki.judgels.jerahmeel.controllers.securities.LoggedIn;
import org.iatoki.judgels.jerahmeel.curriculum.html.createCurriculumView;
import org.iatoki.judgels.jerahmeel.curriculum.html.editCurriculumGeneralView;
import org.iatoki.judgels.jerahmeel.curriculum.html.listCurriculumsView;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.template.HtmlTemplate;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class CurriculumController extends AbstractCurriculumController {

    private static final long PAGE_SIZE = 20;
    private static final String CURRICULUM = "curriculum";

    private final CurriculumService curriculumService;

    @Inject
    public CurriculumController(CurriculumService curriculumService) {
        this.curriculumService = curriculumService;
    }

    @Transactional(readOnly = true)
    public Result viewCurriculums() {
        return listCurriculums(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listCurriculums(long page, String orderBy, String orderDir, String filterString) {
        Page<Curriculum> pageOfCurriculums = curriculumService.getPageOfCurriculums(page, PAGE_SIZE, orderBy, orderDir, filterString);

        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(listCurriculumsView.render(pageOfCurriculums, orderBy, orderDir, filterString));
        template.setMainTitle(Messages.get("curriculum.list"));
        template.addMainButton(Messages.get("commons.create"), routes.CurriculumController.createCurriculum());
        template.setPageTitle("Curriculums");

        return renderTemplate(template);
    }

    public Result jumpToCourses(long curriculumId) {
        return redirect(org.iatoki.judgels.jerahmeel.curriculum.course.routes.CurriculumCourseController.viewCourses(curriculumId));
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createCurriculum() {
        Form<CurriculumUpsertForm> curriculumUpsertForm = Form.form(CurriculumUpsertForm.class);

        return showCreateCurriculum(curriculumUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateCurriculum() {
        Form<CurriculumUpsertForm> curriculumUpsertForm = Form.form(CurriculumUpsertForm.class).bindFromRequest();

        if (formHasErrors(curriculumUpsertForm)) {
            return showCreateCurriculum(curriculumUpsertForm);
        }

        CurriculumUpsertForm curriculumUpsertData = curriculumUpsertForm.get();
        Curriculum curriculum = curriculumService.createCurriculum(curriculumUpsertData.name, curriculumUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(CURRICULUM, curriculum.getJid(), curriculum.getName()));

        return redirect(routes.CurriculumController.viewCurriculums());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editCurriculumGeneral(long curriculumId) throws CurriculumNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        CurriculumUpsertForm curriculumUpsertData = new CurriculumUpsertForm();
        curriculumUpsertData.name = curriculum.getName();
        curriculumUpsertData.description = curriculum.getDescription();

        Form<CurriculumUpsertForm> curriculumUpsertForm = Form.form(CurriculumUpsertForm.class).fill(curriculumUpsertData);

        return showEditCurriculumGeneral(curriculumUpsertForm, curriculum);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditCurriculumGeneral(long curriculumId) throws CurriculumNotFoundException {
        Curriculum curriculum = curriculumService.findCurriculumById(curriculumId);
        Form<CurriculumUpsertForm> curriculumUpsertForm = Form.form(CurriculumUpsertForm.class).bindFromRequest();

        if (formHasErrors(curriculumUpsertForm)) {
            return showEditCurriculumGeneral(curriculumUpsertForm, curriculum);
        }

        CurriculumUpsertForm curriculumUpsertData = curriculumUpsertForm.get();
        curriculumService.updateCurriculum(curriculum.getJid(), curriculumUpsertData.name, curriculumUpsertData.description, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!curriculum.getName().equals(curriculumUpsertData.name)) {
            JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(CURRICULUM, curriculum.getJid(), curriculum.getName(), curriculumUpsertData.name));
        }
        JerahmeelControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(CURRICULUM, curriculum.getJid(), curriculumUpsertData.name));

        return redirect(routes.CurriculumController.viewCurriculums());
    }

    private Result showCreateCurriculum(Form<CurriculumUpsertForm> curriculumUpsertForm) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(createCurriculumView.render(curriculumUpsertForm));
        template.setMainTitle(Messages.get("curriculum.create"));
        template.markBreadcrumbLocation(Messages.get("curriculum.create"), routes.CurriculumController.createCurriculum());
        template.setPageTitle("Curriculum - Create");
        return renderTemplate(template);
    }

    private Result showEditCurriculumGeneral(Form<CurriculumUpsertForm> curriculumUpsertForm, Curriculum curriculum) {
        HtmlTemplate template = getBaseHtmlTemplate();
        template.setContent(editCurriculumGeneralView.render(curriculumUpsertForm, curriculum.getId()));
        appendTabs(template, curriculum);
        template.markBreadcrumbLocation(Messages.get("curriculum.update"), routes.CurriculumController.editCurriculumGeneral(curriculum.getId()));
        template.setPageTitle("Curriculum - Update");
        return renderTemplate(template);
    }
}
