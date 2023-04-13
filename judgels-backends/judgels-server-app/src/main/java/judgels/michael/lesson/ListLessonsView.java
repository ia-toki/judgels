package judgels.michael.lesson;

import java.util.Map;
import judgels.jophiel.api.profile.Profile;
import judgels.michael.template.HtmlTemplate;
import judgels.michael.template.TemplateView;
import judgels.persistence.api.Page;
import judgels.sandalphon.api.lesson.Lesson;

public class ListLessonsView extends TemplateView {
    private final Page<Lesson> lessons;
    private final String filterString;
    private final Map<String, Profile> profilesMap;

    public ListLessonsView(HtmlTemplate template, Page<Lesson> lessons, String filterString, Map<String, Profile> profilesMap) {
        super("listLessonsView.ftl", template);
        this.lessons = lessons;
        this.filterString = filterString;
        this.profilesMap = profilesMap;
    }

    public Page<Lesson> getLessons() {
        return lessons;
    }

    public String getFilterString() {
        return filterString;
    }

    public Map<String, Profile> getProfilesMap() {
        return profilesMap;
    }
}
