package judgels.catalog.lesson.statement;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

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
import judgels.api.catalog.lesson.LessonStatement;
import judgels.api.catalog.lesson.statement.LessonStatementLanguagesResponse;
import judgels.api.catalog.lesson.statement.LessonStatementMediaFilesResponse;
import judgels.catalog.CatalogFiles;
import judgels.catalog.StatementLanguageStatus;
import judgels.catalog.WorldLanguageRegistry;
import judgels.catalog.lesson.LessonAccessChecker;
import judgels.core.JudgelsResponseBuilders;
import judgels.core.api.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v4/lessons/{lessonJid}/statement")
public class LessonStatementResource {
    @Inject protected LessonAccessChecker accessChecker;
    @Inject protected LessonStatementStore statementStore;

    @Inject public LessonStatementResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LessonStatement getStatement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @QueryParam("language") Optional<String> language) {

        String actorJid = accessChecker.checkCanView(authHeader, lessonJid);

        String statementLanguage = language.orElseGet(() -> statementStore.getDefaultLanguage(actorJid, lessonJid));
        checkLanguageEnabled(actorJid, lessonJid, statementLanguage);

        return statementStore.getStatement(actorJid, lessonJid, statementLanguage);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateStatement(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @QueryParam("language") String language,
            LessonStatement statement) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);
        checkLanguageEnabled(actorJid, lessonJid, language);

        statementStore.updateStatement(actorJid, lessonJid, language, statement);
    }

    @GET
    @Path("/languages")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LessonStatementLanguagesResponse getStatementLanguages(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        String actorJid = accessChecker.checkCanView(authHeader, lessonJid);

        LessonStatementLanguagesResponse.Builder response = new LessonStatementLanguagesResponse.Builder()
                .defaultLanguage(statementStore.getDefaultLanguage(actorJid, lessonJid));

        statementStore.getAvailableLanguages(actorJid, lessonJid).forEach((language, status) -> {
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
            @PathParam("lessonJid") String lessonJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);

        // Adding a language writes the default language's statement to it, so it must not be there yet.
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)
                || statementStore.getAvailableLanguages(actorJid, lessonJid).containsKey(language)) {
            throw new BadRequestException();
        }

        statementStore.addLanguage(actorJid, lessonJid, language);
    }

    @POST
    @Path("/languages/{language}/enable")
    @UnitOfWork
    public void enableStatementLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);
        checkLanguageAvailable(actorJid, lessonJid, language);

        statementStore.enableLanguage(actorJid, lessonJid, language);
    }

    @POST
    @Path("/languages/{language}/disable")
    @UnitOfWork
    public void disableStatementLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);
        checkLanguageAvailable(actorJid, lessonJid, language);

        if (language.equals(statementStore.getDefaultLanguage(actorJid, lessonJid))) {
            throw new BadRequestException();
        }

        statementStore.disableLanguage(actorJid, lessonJid, language);
    }

    @POST
    @Path("/languages/{language}/make-default")
    @UnitOfWork
    public void makeStatementLanguageDefault(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("language") String language) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);
        checkLanguageEnabled(actorJid, lessonJid, language);

        statementStore.makeDefaultLanguage(actorJid, lessonJid, language);
    }

    @GET
    @Path("/media")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public LessonStatementMediaFilesResponse getStatementMediaFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        String actorJid = accessChecker.checkCanView(authHeader, lessonJid);

        return new LessonStatementMediaFilesResponse.Builder()
                .data(CatalogFiles.fromFileInfos(statementStore.getStatementMediaFiles(actorJid, lessonJid)))
                .build();
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadStatementMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }
        CatalogFiles.checkFilename(fileDetails.getFileName());

        statementStore.uploadStatementMediaFile(actorJid, lessonJid, fileStream, fileDetails.getFileName());
    }

    @POST
    @Path("/media/zip")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadStatementMediaZip(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @FormDataParam("file") InputStream fileStream) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }

        statementStore.uploadStatementMediaFileZipped(actorJid, lessonJid, fileStream);
    }

    @GET
    @Path("/media/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadStatementMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("filename") String filename) {

        String actorJid = accessChecker.checkCanView(authHeader, lessonJid);
        CatalogFiles.checkFilename(filename);

        String mediaUrl = statementStore.getStatementMediaFileURL(actorJid, lessonJid, filename);
        return JudgelsResponseBuilders.buildDownloadResponse(mediaUrl);
    }

    @DELETE
    @Path("/media/{filename}")
    @UnitOfWork
    public void deleteStatementMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid,
            @PathParam("filename") String filename) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);
        CatalogFiles.checkFilename(filename);

        statementStore.deleteStatementMediaFile(actorJid, lessonJid, filename);
    }

    @DELETE
    @Path("/media")
    @UnitOfWork
    public void deleteStatementMediaFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("lessonJid") String lessonJid) {

        String actorJid = accessChecker.checkCanEdit(authHeader, lessonJid);

        statementStore.deleteAllStatementMediaFiles(actorJid, lessonJid);
    }

    private void checkLanguageAvailable(String actorJid, String lessonJid, String language) {
        Map<String, StatementLanguageStatus> languages = statementStore.getAvailableLanguages(actorJid, lessonJid);
        if (language == null || !languages.containsKey(language)) {
            throw new BadRequestException();
        }
    }

    private void checkLanguageEnabled(String actorJid, String lessonJid, String language) {
        if (language == null || !statementStore.getEnabledLanguages(actorJid, lessonJid).contains(language)) {
            throw new BadRequestException();
        }
    }
}
