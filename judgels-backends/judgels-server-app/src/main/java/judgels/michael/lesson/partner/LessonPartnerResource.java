package judgels.michael.lesson.partner;

import static java.util.stream.Collectors.toSet;
import static judgels.service.ServiceUtils.checkAllowed;
import static judgels.service.ServiceUtils.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.views.View;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import judgels.jophiel.api.actor.Actor;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.lesson.BaseLessonResource;
import judgels.michael.resource.EditPartnersForm;
import judgels.michael.resource.EditPartnersView;
import judgels.michael.resource.ListPartnersView;
import judgels.michael.resource.PartnerUtils;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.api.resource.Partner;
import judgels.sandalphon.lesson.partner.LessonPartnerStore;

@Path("/lessons/{lessonId}/partners")
public class LessonPartnerResource extends BaseLessonResource {
    @Inject protected LessonPartnerStore partnerStore;

    @Inject public LessonPartnerResource() {}

    @GET
    @UnitOfWork(readOnly = true)
    public View listPartners(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.isAuthor(actor, lesson));

        List<Partner> partners = partnerStore.getPartners(lesson.getJid());
        Set<String> userJids = partners.stream().map(Partner::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        HtmlTemplate template = newLessonPartnerTemplate(actor, lesson);
        template.setActiveSecondaryTab("view");
        return new ListPartnersView(template, partners, profilesMap);
    }

    @GET
    @Path("/edit")
    @UnitOfWork(readOnly = true)
    public View editPartners(@Context HttpServletRequest req, @PathParam("lessonId") int lessonId) {
        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.isAuthor(actor, lesson));

        List<Partner> partners = partnerStore.getPartners(lesson.getJid());
        Set<String> userJids = partners.stream().map(Partner::getUserJid).collect(toSet());
        Map<String, Profile> profilesMap = profileStore.getProfiles(userJids);

        EditPartnersForm form = new EditPartnersForm();
        form.csv = PartnerUtils.partnersToCsv(partners, profilesMap);

        return renderEditPartners(actor, lesson, form);
    }

    @POST
    @Path("/edit")
    @UnitOfWork
    public Response updatePartners(
            @Context HttpServletRequest req,
            @PathParam("lessonId") int lessonId,
            @BeanParam EditPartnersForm form) {

        Actor actor = actorChecker.check(req);
        Lesson lesson = checkFound(lessonStore.getLessonById(lessonId));
        checkAllowed(roleChecker.isAuthor(actor, lesson));

        Optional<List<Partner>> partners = PartnerUtils.csvToPartners(form.csv, userStore);
        if (!partners.isPresent()) {
            form.globalError = "Invalid CSV format.";
            return ok(renderEditPartners(actor, lesson, form));
        }

        partnerStore.setPartners(lesson.getJid(), partners.get());

        return redirect("/lessons/" + lessonId + "/partners");
    }

    private View renderEditPartners(Actor actor, Lesson lesson, EditPartnersForm form) {
        HtmlTemplate template = newLessonPartnerTemplate(actor, lesson);
        template.setActiveSecondaryTab("edit");
        return new EditPartnersView(template, form);
    }

    private HtmlTemplate newLessonPartnerTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("partners");
        template.addSecondaryTab("view", "View", "/lessons/" + lesson.getId() + "/partners");
        template.addSecondaryTab("edit", "Edit", "/lessons/" + lesson.getId() + "/partners/edit");
        return template;
    }
}
