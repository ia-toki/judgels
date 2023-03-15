package judgels.michael.problem.base.partner;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditPartnersForm extends HtmlForm {
    @FormParam("csv")
    String csv;

    public String getCsv() {
        return csv;
    }
}
