package judgels.catalog.problem.statement;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import com.google.common.collect.Lists;
import io.dropwizard.hibernate.UnitOfWork;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import judgels.api.catalog.problem.ProblemFile;
import judgels.api.catalog.problem.ProblemStatement;
import judgels.api.catalog.problem.statement.ProblemStatementLanguagesResponse;
import judgels.api.catalog.problem.statement.ProblemStatementMediaFilesResponse;
import judgels.catalog.StatementLanguageStatus;
import judgels.catalog.WorldLanguageRegistry;
import judgels.catalog.problem.ProblemAccessChecker;
import judgels.core.JudgelsResponseBuilders;
import judgels.core.api.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v4/problems/{problemJid}/statement")
public class ProblemStatementResource {
    @Inject protected ProblemAccessChecker accessChecker;
    @Inject protected ProblemStatementStore statementStore;

    @Inject public ProblemStatementResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemStatement getStatement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language) {

        String actorJid = accessChecker.checkCanView(authHeader, problemJid);

        String statementLanguage = language.orElseGet(() -> statementStore.getStatementDefaultLanguage(actorJid, problemJid));
        checkLanguageEnabled(actorJid, problemJid, statementLanguage);

        return statementStore.getStatement(actorJid, problemJid, statementLanguage);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateStatement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") String language,
            ProblemStatement statement) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkLanguageEnabled(actorJid, problemJid, language);

        statementStore.updateStatement(actorJid, problemJid, language, statement);
    }

    @GET
    @Path("/languages")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemStatementLanguagesResponse getStatementLanguages(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = accessChecker.checkCanView(authHeader, problemJid);

        ProblemStatementLanguagesResponse.Builder response = new ProblemStatementLanguagesResponse.Builder()
                .defaultLanguage(statementStore.getStatementDefaultLanguage(actorJid, problemJid));

        statementStore.getStatementAvailableLanguages(actorJid, problemJid).forEach((language, status) -> {
            if (status == StatementLanguageStatus.ENABLED) {
                response.addEnabledLanguages(language);
            } else {
                response.addDisabledLanguages(language);
            }
        });

        return response.build();
    }

    @POST
    @Path("/languages/{language}")
    @UnitOfWork
    public void addStatementLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);

        // Adding a language writes the default language's statement to it, so it must not be there yet.
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)
                || statementStore.getStatementAvailableLanguages(actorJid, problemJid).containsKey(language)) {
            throw new BadRequestException();
        }

        statementStore.addStatementLanguage(actorJid, problemJid, language);
    }

    @POST
    @Path("/languages/{language}/enable")
    @UnitOfWork
    public void enableStatementLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkLanguageAvailable(actorJid, problemJid, language);

        statementStore.enableStatementLanguage(actorJid, problemJid, language);
    }

    @POST
    @Path("/languages/{language}/disable")
    @UnitOfWork
    public void disableStatementLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkLanguageAvailable(actorJid, problemJid, language);

        if (language.equals(statementStore.getStatementDefaultLanguage(actorJid, problemJid))) {
            throw new BadRequestException();
        }

        statementStore.disableStatementLanguage(actorJid, problemJid, language);
    }

    @POST
    @Path("/languages/{language}/make-default")
    @UnitOfWork
    public void makeStatementLanguageDefault(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkLanguageEnabled(actorJid, problemJid, language);

        statementStore.makeStatementDefaultLanguage(actorJid, problemJid, language);
    }

    @GET
    @Path("/media")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemStatementMediaFilesResponse getStatementMediaFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = accessChecker.checkCanView(authHeader, problemJid);

        return new ProblemStatementMediaFilesResponse.Builder()
                .data(Lists.transform(statementStore.getStatementMediaFiles(actorJid, problemJid),
                        f -> new ProblemFile.Builder()
                                .name(f.getName())
                                .size(f.getSize())
                                .lastModifiedTime(f.getLastModifiedTime())
                                .build()))
                .build();
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadStatementMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }
        checkFilename(fileDetails.getFileName());

        statementStore.uploadStatementMediaFile(actorJid, problemJid, fileStream, fileDetails.getFileName());
    }

    @POST
    @Path("/media/zip")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadStatementMediaZip(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }

        statementStore.uploadStatementMediaFileZipped(actorJid, problemJid, fileStream);
    }

    @GET
    @Path("/media/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadStatementMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = accessChecker.checkCanView(authHeader, problemJid);
        checkFilename(filename);

        String mediaUrl = statementStore.getStatementMediaFileURL(actorJid, problemJid, filename);
        return JudgelsResponseBuilders.buildDownloadResponse(mediaUrl);
    }

    @DELETE
    @Path("/media/{filename}")
    @UnitOfWork
    public void deleteStatementMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkFilename(filename);

        statementStore.deleteStatementMediaFile(actorJid, problemJid, filename);
    }

    @DELETE
    @Path("/media")
    @UnitOfWork
    public void deleteStatementMediaFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);

        statementStore.deleteAllStatementMediaFiles(actorJid, problemJid);
    }

    private void checkLanguageAvailable(String actorJid, String problemJid, String language) {
        Map<String, StatementLanguageStatus> languages = statementStore.getStatementAvailableLanguages(actorJid, problemJid);
        if (language == null || !languages.containsKey(language)) {
            throw new BadRequestException();
        }
    }

    private void checkLanguageEnabled(String actorJid, String problemJid, String language) {
        if (language == null || !statementStore.getStatementEnabledLanguages(actorJid, problemJid).contains(language)) {
            throw new BadRequestException();
        }
    }

    // A media file sits directly in the media directory, so its name must not lead anywhere else.
    private static void checkFilename(String filename) {
        if (filename == null
                || filename.isEmpty()
                || filename.startsWith(".")
                || filename.contains("/")
                || filename.contains("\\")) {
            throw new BadRequestException();
        }
    }
}
