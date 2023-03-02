package judgels.michael.lesson;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.lesson.Lesson;

public class BaseLessonResource extends BaseResource {
    protected HtmlTemplate newLessonsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("lessons");
        return template;
    }

    protected HtmlTemplate newLessonTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = this.newLessonsTemplate(actor);
        template.setTitle("#" + lesson.getId() + ": " + lesson.getSlug());
        template.addMainTab("general", "General", "/lessons/" + lesson.getId());
        return template;
    }
}
