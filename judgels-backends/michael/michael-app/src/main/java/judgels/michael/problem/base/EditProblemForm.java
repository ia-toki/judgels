package judgels.michael.problem.base;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditProblemForm extends HtmlForm {
    @FormParam("slug")
    String slug;

    @FormParam("additionalNote")
    String additionalNote;

    @FormParam("writerUsernames")
    String writerUsernames;

    @FormParam("developerUsernames")
    String developerUsernames;

    @FormParam("testerUsernames")
    String testerUsernames;

    @FormParam("editorialistUsernames")
    String editorialistUsernames;

    @FormParam("tags")
    Set<String> tags = new HashSet<>();

    public String getSlug() {
        return slug;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public String getWriterUsernames() {
        return writerUsernames;
    }

    public String getDeveloperUsernames() {
        return developerUsernames;
    }

    public String getTesterUsernames() {
        return testerUsernames;
    }

    public String getEditorialistUsernames() {
        return editorialistUsernames;
    }

    public Set<String> getTags() {
        return tags;
    }
}
