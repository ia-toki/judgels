package judgels.sandalphon.problem.base;

import io.dropwizard.hibernate.UnitOfWork;
import java.util.Optional;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import judgels.sandalphon.api.problem.Problem;
import judgels.service.ServiceUtils;

@Path("/api/v2/problems/{problemJid}")
public class ProblemResource {
    private final ProblemStore problemStore;

    @Inject
    public ProblemResource(ProblemStore problemStore) {
        this.problemStore = problemStore;
    }

    @GET
    @Path("/render/{mediaFilename}")
    @UnitOfWork
    public Response renderStatementImage(
            @HeaderParam("If-Modified-Since") Optional<String> ifModifiedSince,
            @PathParam("problemJid") String problemJid,
            @PathParam("mediaFilename") String mediaFilename) {

        Problem problem = problemStore.findProblemByJid(problemJid);
        String mediaUrl = problemStore.getStatementMediaFileURL(null, problem.getJid(), mediaFilename);

        return ServiceUtils.buildImageResponse(mediaUrl, ifModifiedSince);
    }

    @GET
    @Path("/editorials/render/{mediaFilename}")
    @UnitOfWork
    public Response renderEditorialImage(
            @HeaderParam("If-Modified-Since") Optional<String> ifModifiedSince,
            @PathParam("problemJid") String problemJid,
            @PathParam("mediaFilename") String mediaFilename) {

        Problem problem = problemStore.findProblemByJid(problemJid);
        String mediaUrl = problemStore.getEditorialMediaFileURL(null, problem.getJid(), mediaFilename);

        return ServiceUtils.buildImageResponse(mediaUrl, ifModifiedSince);
    }
}
