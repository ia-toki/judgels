package judgels.michael.lesson;

import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Set;
import judgels.api.actor.Actor;
import judgels.api.lesson.Lesson;
import judgels.lesson.LessonRoleChecker;
import judgels.lesson.LessonStore;
import judgels.lesson.statement.LessonStatementStore;
import judgels.michael.BaseResource;
import judgels.michael.template.HtmlTemplate;
import judgels.profile.ProfileStore;
import judgels.user.UserStore;

public class BaseLessonResource extends BaseResource {
    @Inject protected LessonStore lessonStore;
    @Inject protected LessonStatementStore statementStore;
    @Inject protected LessonRoleChecker roleChecker;
    @Inject protected UserStore userStore;
    @Inject protected ProfileStore profileStore;

    protected String resolveStatementLanguage(HttpServletRequest req, Actor actor, Lesson lesson, Set<String> enabledLanguages) {
        String language = (String) req.getSession().getAttribute("statementLanguage");
        if (language == null || !enabledLanguages.contains(language)) {
            language = statementStore.getDefaultLanguage(actor.getUserJid(), lesson.getJid());
        }

        setCurrentStatementLanguage(req, language);
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
        if (roleChecker.isAuthor(actor, lesson)) {
            template.addMainTab("partners", "Partners", "/lessons/" + lesson.getId() + "/partners");
        }
        if (roleChecker.canEdit(actor, lesson)) {
            template.addMainTab("versions", "Versions", "/lessons/" + lesson.getId() + "/versions/local");
        }

        if (lessonStore.userCloneExists(actor.getUserJid(), lesson.getJid())) {
            template.setMainWarningHtml("Warning: you have <a href=\"/lessons/" + lesson.getId() + "/versions/local\">uncommitted changes</a> for this lesson!");
        }

        return template;
    }
}
