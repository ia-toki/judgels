package judgels.michael.account.user;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class UpsertUsersForm extends HtmlForm {
    @FormParam("csv")
    public String csv = "";

    public String getCsv() {
        return csv;
    }
}
