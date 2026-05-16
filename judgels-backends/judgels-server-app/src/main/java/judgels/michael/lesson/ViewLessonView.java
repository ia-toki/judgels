package judgels.michael.lesson;

import judgels.api.lesson.Lesson;
import judgels.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;

public class ViewLessonView extends TemplateView {
    private final Lesson lesson;
    private final Profile profile;

    public ViewLessonView(HtmlTemplate template, Lesson lesson, Profile profile) {
        super("viewLessonView.ftl", template);
        this.lesson = lesson;
        this.profile = profile;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public Profile getProfile() {
        return profile;
    }
}
