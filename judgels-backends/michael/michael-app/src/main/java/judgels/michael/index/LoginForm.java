package judgels.michael.index;

import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class LoginForm extends HtmlForm {
    @FormParam("username")
    String username;

    @FormParam("password")
    String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
