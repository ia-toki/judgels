package org.iatoki.judgels.sandalphon.grader;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.Authorized;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.grader.html.createGraderView;
import org.iatoki.judgels.sandalphon.grader.html.editGraderView;
import org.iatoki.judgels.sandalphon.grader.html.listGradersView;
import org.iatoki.judgels.sandalphon.grader.html.viewGraderView;
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
public final class GraderController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String GRADER = "grader";

    private final GraderService graderService;

    @Inject
    public GraderController(GraderService graderService) {
        this.graderService = graderService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listGraders(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listGraders(long pageIndex, String orderBy, String orderDir, String filterString) {
        Page<Grader> pageOfGraders = graderService.getPageOfGraders(pageIndex, PAGE_SIZE, orderBy, orderDir, filterString);

        LazyHtml content = new LazyHtml(listGradersView.render(pageOfGraders, orderBy, orderDir, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("grader.list"), new InternalLink(Messages.get("commons.create"), routes.GraderController.createGrader()), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("grader.graders"), routes.GraderController.index())
        ));

        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Graders - List");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result viewGrader(long graderId) throws GraderNotFoundException {
        Grader grader = graderService.findGraderById(graderId);

        LazyHtml content = new LazyHtml(viewGraderView.render(grader));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("grader.grader") + " #" + grader.getId() + ": " + grader.getName(), new InternalLink(Messages.get("commons.update"), routes.GraderController.editGrader(graderId)), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("grader.graders"), routes.GraderController.index()),
                new InternalLink(Messages.get("grader.view"), routes.GraderController.viewGrader(graderId))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Grader - View");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createGrader() {
        Form<GraderUpsertForm> graderUpsertForm = Form.form(GraderUpsertForm.class);

        return showCreateGrader(graderUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateGrader() {
        Form<GraderUpsertForm> graderUpsertForm = Form.form(GraderUpsertForm.class).bindFromRequest();

        if (formHasErrors(graderUpsertForm)) {
            return showCreateGrader(graderUpsertForm);
        }

        GraderUpsertForm graderUpsertData = graderUpsertForm.get();
        Grader grader = graderService.createGrader(graderUpsertData.name, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(GRADER, grader.getJid(), graderUpsertData.name));

        return redirect(routes.GraderController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editGrader(long graderId) throws GraderNotFoundException {
        Grader grader = graderService.findGraderById(graderId);
        GraderUpsertForm graderUpsertData = new GraderUpsertForm();
        graderUpsertData.name = grader.getName();
        Form<GraderUpsertForm> graderUpsertForm = Form.form(GraderUpsertForm.class).fill(graderUpsertData);

        return showEditGrader(graderUpsertForm, grader);
    }

    @Transactional
    public Result postEditGrader(long graderId) throws GraderNotFoundException {
        Grader grader = graderService.findGraderById(graderId);
        Form<GraderUpsertForm> graderUpsertForm = Form.form(GraderUpsertForm.class).bindFromRequest();

        if (formHasErrors(graderUpsertForm)) {
            return showEditGrader(graderUpsertForm, grader);
        }

        GraderUpsertForm graderUpsertData = graderUpsertForm.get();
        graderService.updateGrader(grader.getJid(), graderUpsertData.name, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!grader.getName().equals(graderUpsertData.name)) {
            SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(GRADER, grader.getJid(), grader.getName(), graderUpsertData.name));
        }
        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(GRADER, grader.getJid(), graderUpsertData.name));

        return redirect(routes.GraderController.index());
    }

    private Result showCreateGrader(Form<GraderUpsertForm> graderUpsertForm) {
        LazyHtml content = new LazyHtml(createGraderView.render(graderUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("grader.create"), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("grader.graders"), routes.GraderController.index()),
                new InternalLink(Messages.get("grader.create"), routes.GraderController.createGrader())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Grader - Create");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditGrader(Form<GraderUpsertForm> graderUpsertForm, Grader grader) {
        LazyHtml content = new LazyHtml(editGraderView.render(graderUpsertForm, grader.getId()));
        content.appendLayout(c -> headingLayout.render(Messages.get("grader.grader") + " #" + grader.getId() + ": " + grader.getName(), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("grader.graders"), routes.GraderController.index()),
                new InternalLink(Messages.get("grader.update"), routes.GraderController.editGrader(grader.getId()))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Grader - Update");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }
}
