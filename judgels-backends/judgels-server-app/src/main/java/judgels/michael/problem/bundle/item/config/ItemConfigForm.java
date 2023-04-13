package judgels.michael.problem.bundle.item.config;

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.FormParam;
import judgels.michael.template.HtmlForm;

public class ItemConfigForm extends HtmlForm {
    @FormParam("meta")
    public String meta = "";

    @FormParam("statement")
    String statement = "";

    @FormParam("score")
    double score = 1.0;

    @FormParam("penalty")
    double penalty = 0.0;

    @FormParam("inputValidationRegex")
    String inputValidationRegex = "";

    @FormParam("gradingRegex")
    String gradingRegex = "";

    @FormParam("choiceAliases")
    List<String> choiceAliases = new ArrayList<>();

    @FormParam("choiceContents")
    List<String> choiceContents = new ArrayList<>();

    @FormParam("choiceIsCorrects")
    List<Integer> choiceIsCorrects = new ArrayList<>();

    public String getMeta() {
        return meta;
    }

    public String getStatement() {
        return statement;
    }

    public double getScore() {
        return score;
    }

    public double getPenalty() {
        return penalty;
    }

    public String getInputValidationRegex() {
        return inputValidationRegex;
    }

    public String getGradingRegex() {
        return gradingRegex;
    }

    public List<String> getChoiceAliases() {
        return choiceAliases;
    }

    public List<String> getChoiceContents() {
        return choiceContents;
    }

    public List<Integer> getChoiceIsCorrects() {
        return choiceIsCorrects;
    }
}
