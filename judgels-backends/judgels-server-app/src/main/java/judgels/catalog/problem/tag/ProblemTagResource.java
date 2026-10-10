package judgels.catalog.problem.tag;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import judgels.api.catalog.problem.tag.ProblemTagCategory;
import judgels.api.catalog.problem.tag.ProblemTagOption;
import judgels.api.catalog.problem.tag.ProblemTagsResponse;
import judgels.catalog.problem.ProblemRoleChecker;
import judgels.core.api.AuthHeader;
import judgels.session.ActorChecker;

@Path("/api/v4/problems/tags")
public class ProblemTagResource {
    @Inject protected ActorChecker actorChecker;
    @Inject protected ProblemRoleChecker roleChecker;
    @Inject protected ProblemTagStore tagStore;

    @Inject public ProblemTagResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemTagsResponse getTags(@HeaderParam(AUTHORIZATION) AuthHeader authHeader) {
        String actorJid = actorChecker.check(authHeader);

        Map<String, Integer> tagCounts = tagStore.getTagCounts(roleChecker.isAdmin(actorJid));

        List<ProblemTagOption> topicOptions = ProblemTags.TOPIC_TAGS.stream()
                .filter(tagCounts::containsKey)
                .map(tag -> createOption(tag.substring("topic-".length()), tag, tagCounts))
                .collect(Collectors.toList());

        ProblemTagsResponse.Builder response = new ProblemTagsResponse.Builder()
                .addData(new ProblemTagCategory.Builder()
                        .title("Visibility")
                        .addOptions(createOption("private", "visibility-private", tagCounts))
                        .addOptions(createOption("public", "visibility-public", tagCounts))
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Statement")
                        .addOptions(createOption("has English statement", "statement-en", tagCounts))
                        .build())
                .addData(new ProblemTagCategory.Builder()
                        .title("Editorial")
                        .addOptions(createOption("has no editorial", "editorial-no", tagCounts))
                        .addOptions(createOption("has editorial", "editorial-yes", tagCounts))
                        .addOptions(createOption("has English editorial", "editorial-en", tagCounts))
                        .build());

        if (!topicOptions.isEmpty()) {
            response.addData(new ProblemTagCategory.Builder()
                    .title("Tag")
                    .options(topicOptions)
                    .build());
        }

        return response
                .topicTags(ProblemTags.TOPIC_TAGS)
                .build();
    }

    private static ProblemTagOption createOption(String name, String value, Map<String, Integer> tagCounts) {
        return new ProblemTagOption.Builder()
                .label(name)
                .value(value)
                .count(tagCounts.getOrDefault(value, 0))
                .build();
    }
}
