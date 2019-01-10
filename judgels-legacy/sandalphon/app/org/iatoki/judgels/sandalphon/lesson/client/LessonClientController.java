package org.iatoki.judgels.sandalphon.lesson.client;

import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.sandalphon.lesson.Lesson;
import org.iatoki.judgels.sandalphon.lesson.LessonNotFoundException;
import org.iatoki.judgels.sandalphon.client.Client;
import org.iatoki.judgels.sandalphon.client.ClientService;
import org.iatoki.judgels.sandalphon.client.lesson.ClientLesson;
import org.iatoki.judgels.sandalphon.client.lesson.ClientLessonUpsertForm;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import org.iatoki.judgels.sandalphon.lesson.LessonControllerUtils;
import org.iatoki.judgels.sandalphon.lesson.LessonService;
import org.iatoki.judgels.sandalphon.lesson.client.html.editClientLessonsView;
import org.iatoki.judgels.sandalphon.lesson.client.html.viewClientLessonView;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Singleton
public final class LessonClientController extends AbstractJudgelsController {

    private static final String LESSON = "lesson";
    private static final String CLIENT = "client";

    private final ClientService clientService;
    private final LessonService lessonService;

    @Inject
    public LessonClientController(ClientService clientService, LessonService lessonService) {
        this.clientService = clientService;
        this.lessonService = lessonService;
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editClientLessons(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageClients(lessonService, lesson)) {
            return notFound();
        }

        Form<ClientLessonUpsertForm> clientLessonUpsertForm = Form.form(ClientLessonUpsertForm.class);
        List<ClientLesson> clientLessons = clientService.getClientLessonsByLessonJid(lesson.getJid());
        List<Client> clients = clientService.getClients();

        return showEditClientLessons(clientLessonUpsertForm, lesson, clients, clientLessons);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditClientLessons(long lessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);

        if (!LessonControllerUtils.isAllowedToManageClients(lessonService, lesson)) {
            return notFound();
        }

        Form<ClientLessonUpsertForm> clientLessonUpsertForm = Form.form(ClientLessonUpsertForm.class).bindFromRequest();

        if (formHasErrors(clientLessonUpsertForm)) {
            List<ClientLesson> clientLessons = clientService.getClientLessonsByLessonJid(lesson.getJid());
            List<Client> clients = clientService.getClients();
            return showEditClientLessons(clientLessonUpsertForm, lesson, clients, clientLessons);
        }

        ClientLessonUpsertForm clientLessonUpsertData = clientLessonUpsertForm.get();
        if (!clientService.clientExistsByJid(clientLessonUpsertData.clientJid) || clientService.isClientAuthorizedForLesson(lesson.getJid(), clientLessonUpsertData.clientJid)) {
            List<ClientLesson> clientLessons = clientService.getClientLessonsByLessonJid(lesson.getJid());
            List<Client> clients = clientService.getClients();
            return showEditClientLessons(clientLessonUpsertForm, lesson, clients, clientLessons);
        }

        clientService.createClientLesson(lesson.getJid(), clientLessonUpsertData.clientJid, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.ADD_IN.construct(LESSON, lesson.getJid(), lesson.getSlug(), CLIENT, clientLessonUpsertData.clientJid, clientService.findClientByJid(clientLessonUpsertData.clientJid).getName()));

        return redirect(routes.LessonClientController.editClientLessons(lesson.getId()));
    }

    @Transactional(readOnly = true)
    public Result viewClientLesson(long lessonId, long clientLessonId) throws LessonNotFoundException {
        Lesson lesson = lessonService.findLessonById(lessonId);
        ClientLesson clientLesson = clientService.findClientLessonById(clientLessonId);
        if (!clientLesson.getLessonJid().equals(lesson.getJid())) {
            return notFound();
        }

        LazyHtml content = new LazyHtml(viewClientLessonView.render(lesson, clientLesson));
        LessonControllerUtils.appendTabsLayout(content, lessonService, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        LessonControllerUtils.appendTitleLayout(content, lessonService, lesson);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, lesson, new InternalLink(Messages.get("lesson.client.client"), routes.LessonClientController.viewClientLesson(lessonId, clientLessonId)));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Update Statement");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditClientLessons(Form<ClientLessonUpsertForm> clientLessonUpsertForm, Lesson lesson, List<Client> clients, List<ClientLesson> clientLessons) {
        LazyHtml content = new LazyHtml(editClientLessonsView.render(clientLessonUpsertForm, lesson.getId(), clients, clientLessons));
        LessonControllerUtils.appendTabsLayout(content, lessonService, lesson);
        LessonControllerUtils.appendVersionLocalChangesWarningLayout(content, lessonService, lesson);
        LessonControllerUtils.appendTitleLayout(content, lessonService, lesson);
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        appendBreadcrumbsLayout(content, lesson, new InternalLink(Messages.get("lesson.client.list"), routes.LessonClientController.editClientLessons(lesson.getId())));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Lesson - Update Client");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private void appendBreadcrumbsLayout(LazyHtml content, Lesson lesson, InternalLink lastLink) {
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content,
                LessonControllerUtils.getLessonBreadcrumbsBuilder(lesson)
                .add(new InternalLink(Messages.get("lesson.client"), org.iatoki.judgels.sandalphon.lesson.routes.LessonController.jumpToClients(lesson.getId())))
                .add(lastLink)
                .build()
        );
    }
}
