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
import judgels.service.api.client.BasicAuthHeader;

public class ClientProblemResource implements ClientProblemService {
    @Override
    public ProblemInfo getProblem(BasicAuthHeader authHeader, String problemJid) {
        return new ProblemInfo.Builder()
                .slug("prob")
                .name("The Problem")
                .build();
    }

    @SuppressWarnings("checkstyle:all")
    @Override
    public ProblemStatement getProblemStatement(
            BasicAuthHeader authHeader,
            String problemJid,
            Optional<String> language) {

        return new ProblemStatement.Builder()
                .name("This is a Problem")
                .timeLimit(2000)
                .memoryLimit(65536)
                .text("<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><p>Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?</p>At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat.<p><h3>Subtasks</h3><ul><li>1 &le; N &le;1000</li></ul><p>sfdsfdsfdsf</p><h3>Contoh Masukan</h3><pre>kagi kagi kagi</pre>")
                .sourceKeys(ImmutableMap.of("source", "Source"))
                .gradingEngine("BatchWithSubtasks")
                .gradingLanguageRestriction(LanguageRestriction.noRestriction())
                .build();
    }

    @Override
    public Map<String, ProblemInfo> findProblemsByJids(
            BasicAuthHeader authHeader,
            Optional<String> language,
            Set<String> jids) {

        return ImmutableMap.<String, ProblemInfo>builder()
                .put(
                        "JIDPROGEfn8VTHVfP8SfsLRw5hM",
                        new ProblemInfo.Builder().slug("prob-a").name("Problem A").build())
                .put(
                        "JIDPROGGEcDb6TMv0ugaUuKWe8A",
                        new ProblemInfo.Builder().slug("prob-b").name("Problem B").build())
                .put(
                        "JIDPROGLjE8V6Jl9vzxisJrR8VR",
                        new ProblemInfo.Builder().slug("prob-c").name("Problem C").build())
                .put(
                        "JIDPROGnOBJiVIL3rqcrZBrMEQc",
                        new ProblemInfo.Builder().slug("prob-d").name("Problem D").build())
                .put(
                        "JIDPROGPshlJBeq8nNcY2VPYucq",
                        new ProblemInfo.Builder().slug("prob-e").name("Problem E").build())
                .put(
                        "JIDPROGZs2DLbkJwr6wFUSJvbZR",
                        new ProblemInfo.Builder().slug("prob-f").name("Problem F").build())
                .build();
    }

    @Override
    public Set<String> getProblemLanguagesByJids(BasicAuthHeader authHeader, Set<String> jids) {
        return ImmutableSet.of("id-ID");
    }
}
