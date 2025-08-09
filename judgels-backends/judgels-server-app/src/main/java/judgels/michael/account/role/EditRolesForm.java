package judgels.michael.account.role;

import jakarta.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class EditRolesForm extends HtmlForm {
    @FormParam("csv")
    public String csv;

    public String getCsv() {
        return csv;
    }
}
