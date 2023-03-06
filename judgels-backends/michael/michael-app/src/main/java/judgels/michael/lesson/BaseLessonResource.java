package judgels.michael.lesson;

import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import judgels.jophiel.api.actor.Actor;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;
import judgels.sandalphon.api.lesson.Lesson;
import judgels.sandalphon.lesson.LessonRoleChecker;
import judgels.sandalphon.lesson.LessonStore;

public class BaseLessonResource extends BaseResource {
    @Inject protected LessonStore lessonStore;
    @Inject protected LessonRoleChecker lessonRoleChecker;

    protected String resolveStatementLanguage(HttpServletRequest req, Actor actor, Lesson lesson, Set<String> enabledLanguages) {
        String language = (String) req.getSession().getAttribute("statementLanguage");
        if (language == null || !enabledLanguages.contains(language)) {
            return lessonStore.getDefaultLanguage(actor.getUserJid(), lesson.getJid());
        }
        return language;
    }

    protected HtmlTemplate newLessonsTemplate(Actor actor) {
        HtmlTemplate template = super.newTemplate(actor);
        template.setActiveSidebarMenu("lessons");
        return template;
    }

    protected HtmlTemplate newLessonTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = this.newLessonsTemplate(actor);
        template.setTitle("#" + lesson.getId() + ": " + lesson.getSlug());
        template.addMainTab("general", "General", "/lessons/" + lesson.getId());
        template.addMainTab("statements", "Statements", "/lessons/" + lesson.getId() + "/statements");
        return template;
    }

    protected HtmlTemplate newLessonStatementTemplate(Actor actor, Lesson lesson) {
        HtmlTemplate template = newLessonTemplate(actor, lesson);
        template.setActiveMainTab("statements");
        template.addSecondaryTab("view", "View", "/lessons/" + lesson.getId() + "/statements");
        template.addSecondaryTab("edit", "Edit", "/lessons/" + lesson.getId() + "/statements/edit");
        template.addSecondaryTab("languages", "Languages", "/lessons/" + lesson.getId() + "/statements/languages");
        return template;
    }
}
