package judgels.michael.lesson;

import java.util.Map;
import judgels.api.lesson.Lesson;
import judgels.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;

public class ListLessonsView extends TemplateView {
    private final Page<Lesson> lessons;
    private final String termFilter;
    private final Map<String, Profile> profilesMap;

    public ListLessonsView(HtmlTemplate template, Page<Lesson> lessons, String termFilter, Map<String, Profile> profilesMap) {
        super("listLessonsView.ftl", template);
        this.lessons = lessons;
        this.termFilter = termFilter;
        this.profilesMap = profilesMap;
    }

    public Page<Lesson> getLessons() {
        return lessons;
    }

    public String getTermFilter() {
        return termFilter;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }
}
