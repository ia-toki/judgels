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
    public ProblemStatement getProblemStatement(
            BasicAuthHeader authHeader,
            String problemJid,
            Optional<String> language) {

        return new ProblemStatement.Builder()
                .name("Problem A")
                .timeLimit(2000)
                .memoryLimit(65536)
                .text("<p>Lorem ipsum</p>")
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
