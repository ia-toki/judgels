package org.iatoki.judgels.jerahmeel.curriculum;

import com.google.common.collect.ImmutableList;
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
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
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
public final class CurriculumController extends AbstractJudgelsController {

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

        LazyHtml content = new LazyHtml(listCurriculumsView.render(pageOfCurriculums, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("curriculum.list"), new InternalLink(Messages.get("commons.create"), routes.CurriculumController.createCurriculum()), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content);
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Curriculums");

        return JerahmeelControllerUtils.getInstance().lazyOk(content);
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
        LazyHtml content = new LazyHtml(createCurriculumView.render(curriculumUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("curriculum.create"), c));
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("curriculum.create"), routes.CurriculumController.createCurriculum())
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Curriculum - Create");
        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditCurriculumGeneral(Form<CurriculumUpsertForm> curriculumUpsertForm, Curriculum curriculum) {
        LazyHtml content = new LazyHtml(editCurriculumGeneralView.render(curriculumUpsertForm, curriculum.getId()));
        CurriculumControllerUtils.appendTabLayout(content, curriculum);
        JerahmeelControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content,
                new InternalLink(Messages.get("curriculum.update"), routes.CurriculumController.editCurriculumGeneral(curriculum.getId()))
        );
        JerahmeelControllerUtils.getInstance().appendTemplateLayout(content, "Curriculum - Update");
        return JerahmeelControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, InternalLink... lastLinks) {
        ImmutableList.Builder<InternalLink> breadcrumbsBuilder = CurriculumControllerUtils.getBreadcrumbsBuilder();
        breadcrumbsBuilder.add(lastLinks);

        JerahmeelControllerUtils.getInstance().appendBreadcrumbsLayout(content, breadcrumbsBuilder.build());
    }
}
