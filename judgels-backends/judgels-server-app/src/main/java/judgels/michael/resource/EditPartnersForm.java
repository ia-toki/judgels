package judgels.michael.resource;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditPartnersForm extends HtmlForm {
    @FormParam("csv")
    public String csv;

    public String getCsv() {
        return csv;
    }
}
