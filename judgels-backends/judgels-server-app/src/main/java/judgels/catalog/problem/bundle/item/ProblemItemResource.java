package judgels.catalog.problem.bundle.item;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static judgels.core.JudgelsRequestChecks.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.catalog.problem.bundle.BundleItem;
import judgels.api.catalog.problem.bundle.Item;
import judgels.api.catalog.problem.bundle.ItemConfig;
import judgels.api.catalog.problem.bundle.MultipleChoiceItemConfig;
import judgels.api.catalog.problem.bundle.ShortAnswerItemConfig;
import judgels.api.catalog.problem.bundle.item.ProblemItemCreateData;
import judgels.api.catalog.problem.bundle.item.ProblemItemUpdateData;
import judgels.api.catalog.problem.bundle.item.ProblemItemsResponse;
import judgels.catalog.problem.ProblemAccessChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.statement.ProblemStatementStore;
import judgels.core.api.AuthHeader;

@Path("/api/v4/problems/{problemJid}/items")
public class ProblemItemResource {
    @Inject protected ProblemAccessChecker accessChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProblemStatementStore statementStore;
    @Inject protected BundleItemStore itemStore;

    @Inject public ProblemItemResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemItemsResponse getItems(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language) {

        String actorJid = checkCanView(authHeader, problemJid);

        String defaultLanguage = statementStore.getStatementDefaultLanguage(actorJid, problemJid);
        String itemLanguage = language.orElse(defaultLanguage);
        checkLanguageEnabled(actorJid, problemJid, itemLanguage);

        ProblemItemsResponse.Builder response = new ProblemItemsResponse.Builder();
        for (BundleItem item : itemStore.getNumberedItems(actorJid, problemJid)) {
            response.addData(toItem(actorJid, problemJid, item, itemLanguage, defaultLanguage));
        }
        return response.build();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @UnitOfWork
    public Item createItem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            ProblemItemCreateData data) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        // A new item starts in the default language, which every other language falls back to.
        String defaultLanguage = statementStore.getStatementDefaultLanguage(actorJid, problemJid);
        ItemConfig config = ItemEngineRegistry.getByType(data.getType()).createDefaultConfig();
        BundleItem item = itemStore.createItem(actorJid, problemJid, data.getType(), config, defaultLanguage);

        return toItem(
                actorJid,
                problemJid,
                checkFound(itemStore.getNumberedItem(actorJid, problemJid, item.getJid())),
                defaultLanguage,
                defaultLanguage);
    }

    @GET
    @Path("/{itemJid}")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public Item getItem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("itemJid") String itemJid,
            @QueryParam("language") Optional<String> language) {

        String actorJid = checkCanView(authHeader, problemJid);
        BundleItem item = checkFound(itemStore.getNumberedItem(actorJid, problemJid, itemJid));

        String defaultLanguage = statementStore.getStatementDefaultLanguage(actorJid, problemJid);
        String itemLanguage = language.orElse(defaultLanguage);
        checkLanguageEnabled(actorJid, problemJid, itemLanguage);

        return toItem(actorJid, problemJid, item, itemLanguage, defaultLanguage);
    }

    @PUT
    @Path("/{itemJid}")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateItem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("itemJid") String itemJid,
            @QueryParam("language") String language,
            ProblemItemUpdateData data) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        BundleItem item = checkFound(itemStore.getNumberedItem(actorJid, problemJid, itemJid));
        checkLanguageEnabled(actorJid, problemJid, language);

        // The config was written for the type it names, which must be the item's.
        if (data.getType() != item.getType()) {
            throw new BadRequestException();
        }
        checkConfig(data.getConfig());

        itemStore.updateItem(actorJid, problemJid, item, data.getMeta(), data.getConfig(), language);
    }

    @POST
    @Path("/{itemJid}/move-up")
    @UnitOfWork
    public void moveItemUp(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("itemJid") String itemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkFound(itemStore.getNumberedItem(actorJid, problemJid, itemJid));

        itemStore.moveItemUp(actorJid, problemJid, itemJid);
    }

    @POST
    @Path("/{itemJid}/move-down")
    @UnitOfWork
    public void moveItemDown(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("itemJid") String itemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkFound(itemStore.getNumberedItem(actorJid, problemJid, itemJid));

        itemStore.moveItemDown(actorJid, problemJid, itemJid);
    }

    @DELETE
    @Path("/{itemJid}")
    @UnitOfWork
    public void deleteItem(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("itemJid") String itemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkFound(itemStore.getNumberedItem(actorJid, problemJid, itemJid));

        itemStore.removeItem(actorJid, problemJid, itemJid);
    }

    private String checkCanView(AuthHeader authHeader, String problemJid) {
        String actorJid = accessChecker.checkCanView(authHeader, problemJid);
        checkBundle(problemJid);
        return actorJid;
    }

    private String checkCanEdit(AuthHeader authHeader, String problemJid) {
        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkBundle(problemJid);
        return actorJid;
    }

    // Only a bundle problem is made of items.
    private void checkBundle(String problemJid) {
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        if (problem.getType() != ProblemType.BUNDLE) {
            throw new NotFoundException();
        }
    }

    private void checkLanguageEnabled(String actorJid, String problemJid, String language) {
        if (language == null || !statementStore.getStatementEnabledLanguages(actorJid, problemJid).contains(language)) {
            throw new BadRequestException();
        }
    }

    // Grading matches an answer against these, so a config that cannot be matched against is rejected here.
    private static void checkConfig(ItemConfig config) {
        if (config instanceof MultipleChoiceItemConfig) {
            Set<String> aliases = new HashSet<>();
            for (MultipleChoiceItemConfig.Choice choice : ((MultipleChoiceItemConfig) config).getChoices()) {
                if (choice.getAlias().isEmpty() || !aliases.add(choice.getAlias())) {
                    throw new BadRequestException();
                }
            }
        } else if (config instanceof ShortAnswerItemConfig) {
            ShortAnswerItemConfig shortAnswerConfig = (ShortAnswerItemConfig) config;
            checkRegex(shortAnswerConfig.getInputValidationRegex());
            shortAnswerConfig.getGradingRegex().ifPresent(ProblemItemResource::checkRegex);
        }
    }

    private static void checkRegex(String regex) {
        try {
            Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new BadRequestException();
        }
    }

    private Item toItem(String actorJid, String problemJid, BundleItem item, String language, String defaultLanguage) {
        return new Item.Builder()
                .jid(item.getJid())
                .type(item.getType())
                .number(item.getNumber())
                .meta(item.getMeta())
                .config(itemStore.getItemConfig(actorJid, problemJid, item, language, defaultLanguage))
                .build();
    }
}
