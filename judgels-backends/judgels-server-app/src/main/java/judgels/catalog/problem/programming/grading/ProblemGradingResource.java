package judgels.catalog.problem.programming.grading;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static judgels.core.JudgelsRequestChecks.checkFound;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import judgels.api.catalog.problem.Problem;
import judgels.api.catalog.problem.ProblemType;
import judgels.api.catalog.problem.programming.grading.ProblemGradingConfig;
import judgels.api.catalog.problem.programming.grading.ProblemGradingEngineUpdateData;
import judgels.api.catalog.problem.programming.grading.ProblemGradingFilesResponse;
import judgels.catalog.CatalogFiles;
import judgels.catalog.problem.ProblemAccessChecker;
import judgels.catalog.problem.ProblemStore;
import judgels.catalog.problem.programming.ProgrammingProblemStore;
import judgels.core.JudgelsResponseBuilders;
import judgels.core.api.AuthHeader;
import judgels.grading.api.GradingConfig;
import judgels.grading.api.LanguageRestriction;
import judgels.grading.engines.GradingEngineRegistry;
import judgels.grading.languages.GradingLanguageRegistry;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v4/problems/{problemJid}/grading")
public class ProblemGradingResource {
    @Inject protected ProblemAccessChecker accessChecker;
    @Inject protected ProblemStore problemStore;
    @Inject protected ProgrammingProblemStore programmingProblemStore;
    @Inject protected ObjectMapper mapper;

    @Inject public ProblemGradingResource() {}

    @PUT
    @Path("/engine")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateGradingEngine(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            ProblemGradingEngineUpdateData data) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        String engine = data.getEngine();
        if (!GradingEngineRegistry.getInstance().getNamesMap().containsKey(engine)) {
            throw new BadRequestException();
        }

        // A config belongs to its engine, so a new engine starts from its default config.
        if (!engine.equals(programmingProblemStore.getGradingEngine(actorJid, problemJid))) {
            GradingConfig config = GradingEngineRegistry.getInstance().get(engine).createDefaultConfig();
            programmingProblemStore.updateGradingConfig(actorJid, problemJid, config);
            programmingProblemStore.updateGradingEngine(actorJid, problemJid, engine);
        }
    }

    @GET
    @Path("/config")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemGradingConfig getGradingConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanView(authHeader, problemJid);

        return toGradingConfig(
                programmingProblemStore.getGradingEngine(actorJid, problemJid),
                programmingProblemStore.getGradingConfig(actorJid, problemJid));
    }

    @PUT
    @Path("/config")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateGradingConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            ProblemGradingConfig data) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        // The config was written for the engine it names, which may have changed since it was read.
        String engine = programmingProblemStore.getGradingEngine(actorJid, problemJid);
        if (!engine.equals(data.getEngine())) {
            throw new BadRequestException();
        }

        GradingConfig config;
        try {
            config = GradingEngineRegistry.getInstance().get(engine)
                    .parseConfig(mapper, mapper.writeValueAsString(data.getConfig()));
        } catch (IOException e) {
            throw new BadRequestException();
        }

        programmingProblemStore.updateGradingConfig(actorJid, problemJid, config);
    }

    @POST
    @Path("/config/auto-populate")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemGradingConfig autoPopulateGradingConfig(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        // This only proposes a config, so it writes nothing and needs no clone.
        String actorJid = checkCanView(authHeader, problemJid);

        String engine = programmingProblemStore.getGradingEngine(actorJid, problemJid);
        GradingConfig config = GradingConfigAutoPopulatorRegistry.getInstance().get(engine).autoPopulateTestData(
                programmingProblemStore.getGradingConfig(actorJid, problemJid),
                programmingProblemStore.getGradingTestDataFiles(actorJid, problemJid));

        return toGradingConfig(engine, config);
    }

    @GET
    @Path("/test-data")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemGradingFilesResponse getGradingTestDataFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanView(authHeader, problemJid);

        return new ProblemGradingFilesResponse.Builder()
                .data(CatalogFiles.fromFileInfos(programmingProblemStore.getGradingTestDataFiles(actorJid, problemJid)))
                .build();
    }

    @POST
    @Path("/test-data")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadGradingTestDataFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }
        CatalogFiles.checkFilename(fileDetails.getFileName());

        programmingProblemStore.uploadGradingTestDataFile(actorJid, problemJid, fileStream, fileDetails.getFileName());
    }

    @POST
    @Path("/test-data/zip")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadGradingTestDataZip(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }

        programmingProblemStore.uploadGradingTestDataFileZipped(actorJid, problemJid, fileStream);
    }

    @GET
    @Path("/test-data/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadGradingTestDataFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = checkCanView(authHeader, problemJid);
        CatalogFiles.checkFilename(filename);

        String fileUrl = programmingProblemStore.getGradingTestDataFileURL(actorJid, problemJid, filename);
        return JudgelsResponseBuilders.buildDownloadResponse(fileUrl);
    }

    @DELETE
    @Path("/test-data/{filename}")
    @UnitOfWork
    public void deleteGradingTestDataFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        CatalogFiles.checkFilename(filename);

        programmingProblemStore.deleteGradingTestDataFile(actorJid, problemJid, filename);
    }

    @DELETE
    @Path("/test-data")
    @UnitOfWork
    public void deleteGradingTestDataFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        programmingProblemStore.deleteAllGradingTestDataFiles(actorJid, problemJid);
    }

    @GET
    @Path("/helpers")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemGradingFilesResponse getGradingHelperFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanView(authHeader, problemJid);

        return new ProblemGradingFilesResponse.Builder()
                .data(CatalogFiles.fromFileInfos(programmingProblemStore.getGradingHelperFiles(actorJid, problemJid)))
                .build();
    }

    @POST
    @Path("/helpers")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadGradingHelperFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }
        CatalogFiles.checkFilename(fileDetails.getFileName());

        programmingProblemStore.uploadGradingHelperFile(actorJid, problemJid, fileStream, fileDetails.getFileName());
    }

    @POST
    @Path("/helpers/zip")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadGradingHelperZip(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }

        programmingProblemStore.uploadGradingHelperFileZipped(actorJid, problemJid, fileStream);
    }

    @GET
    @Path("/helpers/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadGradingHelperFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = checkCanView(authHeader, problemJid);
        CatalogFiles.checkFilename(filename);

        String fileUrl = programmingProblemStore.getGradingHelperFileURL(actorJid, problemJid, filename);
        return JudgelsResponseBuilders.buildDownloadResponse(fileUrl);
    }

    @DELETE
    @Path("/helpers/{filename}")
    @UnitOfWork
    public void deleteGradingHelperFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        CatalogFiles.checkFilename(filename);

        programmingProblemStore.deleteGradingHelperFile(actorJid, problemJid, filename);
    }

    @DELETE
    @Path("/helpers")
    @UnitOfWork
    public void deleteGradingHelperFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        programmingProblemStore.deleteAllGradingHelperFiles(actorJid, problemJid);
    }

    @GET
    @Path("/language-restriction")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LanguageRestriction getGradingLanguageRestriction(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanView(authHeader, problemJid);

        return programmingProblemStore.getLanguageRestriction(actorJid, problemJid);
    }

    @PUT
    @Path("/language-restriction")
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateGradingLanguageRestriction(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            LanguageRestriction languageRestriction) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (!GradingLanguageRegistry.getInstance().getLanguages().keySet()
                .containsAll(languageRestriction.getAllowedLanguageNames())) {
            throw new BadRequestException();
        }

        programmingProblemStore.updateLanguageRestriction(actorJid, problemJid, languageRestriction);
    }

    private String checkCanView(AuthHeader authHeader, String problemJid) {
        String actorJid = accessChecker.checkCanView(authHeader, problemJid);
        checkProgramming(problemJid);
        return actorJid;
    }

    private String checkCanEdit(AuthHeader authHeader, String problemJid) {
        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkProgramming(problemJid);
        return actorJid;
    }

    // Only a programming problem is graded by an engine; a bundle problem has no grading files.
    private void checkProgramming(String problemJid) {
        Problem problem = checkFound(problemStore.getProblemByJid(problemJid));
        if (problem.getType() != ProblemType.PROGRAMMING) {
            throw new NotFoundException();
        }
    }

    private ProblemGradingConfig toGradingConfig(String engine, GradingConfig config) {
        return new ProblemGradingConfig.Builder()
                .engine(engine)
                .config(mapper.valueToTree(config))
                .build();
    }
}
