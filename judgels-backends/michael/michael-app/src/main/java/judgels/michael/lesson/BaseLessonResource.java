package judgels.michael.lesson;

import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;

public class BaseLessonResource extends BaseResource {
    protected HtmlTemplate newLessonsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("lessons");
        return template;
    }

}
