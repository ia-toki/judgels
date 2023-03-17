package judgels.michael.resource;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class CommitVersionForm extends HtmlForm {
    public String localChangesError = "";

    @FormParam("title")
    public String title;

    @FormParam("description")
    public String description;

    public String getLocalChangesError() {
        return localChangesError;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
