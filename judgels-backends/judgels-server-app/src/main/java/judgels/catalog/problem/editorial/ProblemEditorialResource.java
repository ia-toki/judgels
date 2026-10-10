package judgels.catalog.problem.editorial;

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
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Optional;
import judgels.api.catalog.problem.ProblemEditorial;
import judgels.api.catalog.problem.editorial.ProblemEditorialCreateData;
import judgels.api.catalog.problem.editorial.ProblemEditorialLanguagesResponse;
import judgels.api.catalog.problem.editorial.ProblemEditorialMediaFilesResponse;
import judgels.catalog.StatementLanguageStatus;
import judgels.catalog.WorldLanguageRegistry;
import judgels.catalog.problem.ProblemAccessChecker;
import judgels.catalog.problem.ProblemFiles;
import judgels.core.JudgelsResponseBuilders;
import judgels.core.api.AuthHeader;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/api/v4/problems/{problemJid}/editorial")
public class ProblemEditorialResource {
    @Inject protected ProblemAccessChecker accessChecker;
    @Inject protected ProblemEditorialStore editorialStore;

    @Inject public ProblemEditorialResource() {}

    @GET
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemEditorial getEditorial(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") Optional<String> language) {

        String actorJid = checkCanView(authHeader, problemJid);

        String editorialLanguage = language.orElseGet(() -> editorialStore.getEditorialDefaultLanguage(actorJid, problemJid));
        checkLanguageEnabled(actorJid, problemJid, editorialLanguage);

        return editorialStore.getEditorial(actorJid, problemJid, editorialLanguage);
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void createEditorial(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            ProblemEditorialCreateData data) {

        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);

        // Creating an editorial writes an empty one, so there must be none yet.
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(data.getInitialLanguage())
                || editorialStore.hasEditorial(actorJid, problemJid)) {
            throw new BadRequestException();
        }

        editorialStore.initEditorials(actorJid, problemJid, data.getInitialLanguage());
    }

    @PUT
    @Consumes(APPLICATION_JSON)
    @UnitOfWork
    public void updateEditorial(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @QueryParam("language") String language,
            ProblemEditorial editorial) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkLanguageEnabled(actorJid, problemJid, language);

        editorialStore.updateEditorial(actorJid, problemJid, language, editorial);
    }

    @GET
    @Path("/languages")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemEditorialLanguagesResponse getEditorialLanguages(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanView(authHeader, problemJid);

        ProblemEditorialLanguagesResponse.Builder response = new ProblemEditorialLanguagesResponse.Builder()
                .defaultLanguage(editorialStore.getEditorialDefaultLanguage(actorJid, problemJid));

        editorialStore.getEditorialAvailableLanguages(actorJid, problemJid).forEach((language, status) -> {
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
    public void addEditorialLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        // Adding a language writes an empty editorial to it, so it must not be there yet.
        if (!WorldLanguageRegistry.getInstance().getLanguages().containsKey(language)
                || editorialStore.getEditorialAvailableLanguages(actorJid, problemJid).containsKey(language)) {
            throw new BadRequestException();
        }

        editorialStore.addEditorialLanguage(actorJid, problemJid, language);
    }

    @POST
    @Path("/languages/{language}/enable")
    @UnitOfWork
    public void enableEditorialLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkLanguageAvailable(actorJid, problemJid, language);

        editorialStore.enableEditorialLanguage(actorJid, problemJid, language);
    }

    @POST
    @Path("/languages/{language}/disable")
    @UnitOfWork
    public void disableEditorialLanguage(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkLanguageAvailable(actorJid, problemJid, language);

        if (language.equals(editorialStore.getEditorialDefaultLanguage(actorJid, problemJid))) {
            throw new BadRequestException();
        }

        editorialStore.disableEditorialLanguage(actorJid, problemJid, language);
    }

    @POST
    @Path("/languages/{language}/make-default")
    @UnitOfWork
    public void makeEditorialLanguageDefault(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("language") String language) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        checkLanguageEnabled(actorJid, problemJid, language);

        editorialStore.makeEditorialDefaultLanguage(actorJid, problemJid, language);
    }

    @GET
    @Path("/media")
    @Produces(APPLICATION_JSON)
    @UnitOfWork(readOnly = true)
    public ProblemEditorialMediaFilesResponse getEditorialMediaFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanView(authHeader, problemJid);

        return new ProblemEditorialMediaFilesResponse.Builder()
                .data(ProblemFiles.fromFileInfos(editorialStore.getEditorialMediaFiles(actorJid, problemJid)))
                .build();
    }

    @POST
    @Path("/media")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadEditorialMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream,
            @FormDataParam("file") FormDataContentDisposition fileDetails) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }
        ProblemFiles.checkFilename(fileDetails.getFileName());

        editorialStore.uploadEditorialMediaFile(actorJid, problemJid, fileStream, fileDetails.getFileName());
    }

    @POST
    @Path("/media/zip")
    @Consumes(MULTIPART_FORM_DATA)
    @UnitOfWork
    public void uploadEditorialMediaZip(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @FormDataParam("file") InputStream fileStream) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        if (fileStream == null) {
            throw new BadRequestException();
        }

        editorialStore.uploadEditorialMediaFileZipped(actorJid, problemJid, fileStream);
    }

    @GET
    @Path("/media/{filename}")
    @UnitOfWork(readOnly = true)
    public Response downloadEditorialMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = checkCanView(authHeader, problemJid);
        ProblemFiles.checkFilename(filename);

        String mediaUrl = editorialStore.getEditorialMediaFileURL(actorJid, problemJid, filename);
        return JudgelsResponseBuilders.buildDownloadResponse(mediaUrl);
    }

    @DELETE
    @Path("/media/{filename}")
    @UnitOfWork
    public void deleteEditorialMediaFile(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid,
            @PathParam("filename") String filename) {

        String actorJid = checkCanEdit(authHeader, problemJid);
        ProblemFiles.checkFilename(filename);

        editorialStore.deleteEditorialMediaFile(actorJid, problemJid, filename);
    }

    @DELETE
    @Path("/media")
    @UnitOfWork
    public void deleteEditorialMediaFiles(
            @HeaderParam(AUTHORIZATION) AuthHeader authHeader,
            @PathParam("problemJid") String problemJid) {

        String actorJid = checkCanEdit(authHeader, problemJid);

        editorialStore.deleteAllEditorialMediaFiles(actorJid, problemJid);
    }

    private String checkCanView(AuthHeader authHeader, String problemJid) {
        String actorJid = accessChecker.checkCanView(authHeader, problemJid);
        checkEditorialExists(actorJid, problemJid);
        return actorJid;
    }

    private String checkCanEdit(AuthHeader authHeader, String problemJid) {
        String actorJid = accessChecker.checkCanEdit(authHeader, problemJid);
        checkEditorialExists(actorJid, problemJid);
        return actorJid;
    }

    // A problem has no editorial until one is created.
    private void checkEditorialExists(String actorJid, String problemJid) {
        if (!editorialStore.hasEditorial(actorJid, problemJid)) {
            throw new NotFoundException();
        }
    }

    private void checkLanguageAvailable(String actorJid, String problemJid, String language) {
        if (language == null || !editorialStore.getEditorialAvailableLanguages(actorJid, problemJid).containsKey(language)) {
            throw new BadRequestException();
        }
    }

    private void checkLanguageEnabled(String actorJid, String problemJid, String language) {
        if (language == null || !editorialStore.getEditorialEnabledLanguages(actorJid, problemJid).contains(language)) {
            throw new BadRequestException();
        }
    }
}
