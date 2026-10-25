package judgels.catalog.problem;

import static judgels.core.JudgelsRequestChecks.checkFound;

import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import java.util.Optional;
import judgels.catalog.CatalogFiles;
import judgels.catalog.problem.editorial.ProblemEditorialStore;
import judgels.catalog.problem.statement.ProblemStatementStore;
import judgels.core.JudgelsResponseBuilders;

@Path("/api/v4/problems/{problemJid}/clones/{userJid}")
public class ProblemCloneRenderResource {
    private final ProblemStore problemStore;
    private final ProblemStatementStore statementStore;
    private final ProblemEditorialStore editorialStore;

    @Inject
    public ProblemCloneRenderResource(ProblemStore problemStore, ProblemStatementStore statementStore, ProblemEditorialStore editorialStore) {
        this.problemStore = problemStore;
        this.statementStore = statementStore;
        this.editorialStore = editorialStore;
    }

    @GET
    @Path("/render/{mediaFilename}")
    @UnitOfWork(readOnly = true)
    public Response renderStatementMediaFile(
            @HeaderParam("If-Modified-Since") Optional<String> ifModifiedSince,
            @PathParam("problemJid") String problemJid,
            @PathParam("userJid") String userJid,
            @PathParam("mediaFilename") String mediaFilename) {

        checkFound(problemStore.getProblemByJid(problemJid));
        CatalogFiles.checkFilename(userJid);
        CatalogFiles.checkFilename(mediaFilename);
        String mediaUrl = statementStore.getStatementMediaFileURL(userJid, problemJid, mediaFilename);

        return JudgelsResponseBuilders.buildMediaResponse(mediaUrl, ifModifiedSince);
    }

    @GET
    @Path("/editorials/render/{mediaFilename}")
    @UnitOfWork(readOnly = true)
    public Response renderEditorialMediaFile(
            @HeaderParam("If-Modified-Since") Optional<String> ifModifiedSince,
            @PathParam("problemJid") String problemJid,
            @PathParam("userJid") String userJid,
            @PathParam("mediaFilename") String mediaFilename) {

        checkFound(problemStore.getProblemByJid(problemJid));
        CatalogFiles.checkFilename(userJid);
        CatalogFiles.checkFilename(mediaFilename);
        String mediaUrl = editorialStore.getEditorialMediaFileURL(userJid, problemJid, mediaFilename);

        return JudgelsResponseBuilders.buildMediaResponse(mediaUrl, ifModifiedSince);
    }
}
