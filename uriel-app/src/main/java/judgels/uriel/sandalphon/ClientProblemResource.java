package judgels.uriel.sandalphon;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import judgels.gabriel.api.LanguageRestriction;
import judgels.sandalphon.api.client.problem.ClientProblemService;
import judgels.sandalphon.api.problem.ProblemInfo;
import judgels.sandalphon.api.problem.ProblemStatement;
import judgels.sandalphon.api.problem.ProblemSubmissionConfig;
import judgels.sandalphon.api.problem.ProblemWorksheet;
import judgels.service.api.client.BasicAuthHeader;

@SuppressWarnings("checkstyle:all")
public class ClientProblemResource implements ClientProblemService {
    private static final Map<String, ProblemInfo> problemsByJid = ImmutableMap.<String, ProblemInfo>builder()
            .put(
                    "JIDPROGEfn8VTHVfP8SfsLRw5hM",
                    new ProblemInfo.Builder().slug("prob-a").defaultLanguage("en").putNamesByLanguage("en", "Problem A").putNamesByLanguage("id", "Soal A").build())
            .put(
                    "JIDPROGGEcDb6TMv0ugaUuKWe8A",
                    new ProblemInfo.Builder().slug("prob-b").defaultLanguage("en").putNamesByLanguage("en", "Problem B").build())
            .put(
                    "JIDPROGLjE8V6Jl9vzxisJrR8VR",
                    new ProblemInfo.Builder().slug("prob-c").defaultLanguage("en").putNamesByLanguage("en", "Problem C").build())
            .put(
                    "JIDPROGnOBJiVIL3rqcrZBrMEQc",
                    new ProblemInfo.Builder().slug("prob-d").defaultLanguage("en").putNamesByLanguage("en", "Problem D").build())
            .put(
                    "JIDPROGPshlJBeq8nNcY2VPYucq",
                    new ProblemInfo.Builder().slug("prob-e").defaultLanguage("en").putNamesByLanguage("en", "Problem E").build())
            .put(
                    "JIDPROGZs2DLbkJwr6wFUSJvbZR",
                    new ProblemInfo.Builder().slug("prob-f").defaultLanguage("id").putNamesByLanguage("id", "Soal F").build())
            .build();

    @Override
    public ProblemInfo getProblem(BasicAuthHeader authHeader, String problemJid) {
        return problemsByJid.get(problemJid);
    }

    @Override
    public ProblemSubmissionConfig getProblemSubmissionConfig(BasicAuthHeader authHeader, String problemJid) {
        return new ProblemSubmissionConfig.Builder()
                .sourceKeys(ImmutableMap.of("encoder", "Encoder", "decoder", "Decoder"))
                .gradingEngine("BatchWithSubtasks")
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .build();
    }

    @Override
    public ProblemWorksheet getProblemWorksheet(
            BasicAuthHeader authHeader,
            String problemJid,
            Optional<String> language) {

        String finalLanguage = problemsByJid.get(problemJid).getDefaultLanguage();
        if (language.isPresent() && problemsByJid.get(problemJid).getNamesByLanguage().containsKey(language.get())) {
            finalLanguage = language.get();
        }

        String en = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?</p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.<p><h3>Subtasks</h3><ul><li>1 &le; N &le;1000</li></ul><p>sfdsfdsfdsf</p><h3>Contoh Masukan</h3><pre>kagi kagi kagi</pre>";
        String id = "<p>Halo Dunia!</p>";

        String text;
        if (finalLanguage.equals("en")) {
            text = en;
        } else {
            text = id;
        }

        ProblemStatement statement = new ProblemStatement.Builder()
                .text(text)
                .name(problemsByJid.get(problemJid).getNamesByLanguage().get(finalLanguage))
                .timeLimit(2000)
                .memoryLimit(65536)
                .build();

        ProblemSubmissionConfig submissionConfig = new ProblemSubmissionConfig.Builder()
                .sourceKeys(ImmutableMap.of("encoder", "Encoder", "decoder", "Decoder"))
                .gradingEngine("BatchWithSubtasks")
                .gradingLanguageRestriction(LanguageRestriction.of(ImmutableSet.of("C", "Pascal")))
                .build();

        return new ProblemWorksheet.Builder()
                .statement(statement)
                .submissionConfig(submissionConfig)
                .build();
    }

    @Override
    public Map<String, ProblemInfo> findProblemsByJids(
            BasicAuthHeader authHeader,
            Set<String> jids) {

        return problemsByJid;
    }
}
