package judgels.api;

import static judgels.api.catalog.lesson.LessonErrors.SLUG_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.lesson.LessonCreateData;
import judgels.api.catalog.lesson.LessonResponse;
import judgels.api.catalog.lesson.LessonUpdateData;
import judgels.client.LessonClient;
import judgels.client.LessonClient.GetLessonsParams;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class LessonApiIntegrationTests extends BaseJudgelsApiIntegrationTests {
    private final LessonClient lessonClient = createClient(LessonClient.class);

    @BeforeAll
    static void setUpWebTarget() {
        webTarget = createWebTarget();
    }

    @Test
    void end_to_end_flow() {
        // as admin

        Lesson lessonA = lessonClient.createLesson(adminToken, new LessonCreateData.Builder()
                .slug("lesson-a")
                .additionalNote("This is lesson A")
                .initialLanguage("en-US")
                .build());

        assertThat(lessonA.getSlug()).isEqualTo("lesson-a");
        assertThat(lessonA.getAdditionalNote()).isEqualTo("This is lesson A");
        assertThat(lessonA.getAuthorJid()).isEqualTo(admin.getJid());

        Lesson lessonB = createLessonViaMichael(userToken, "lesson-b");

        assertBadRequest(() -> lessonClient
                .createLesson(adminToken, new LessonCreateData.Builder()
                        .slug("lesson-a")
                        .additionalNote("")
                        .initialLanguage("en-US")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        assertBadRequest(() -> lessonClient
                .createLesson(adminToken, new LessonCreateData.Builder()
                        .slug("lesson-c")
                        .additionalNote("")
                        .initialLanguage("bogus")
                        .build()));

        LessonResponse lessonResponse = lessonClient.getLesson(adminToken, lessonA.getJid());
        assertThat(lessonResponse.getData()).isEqualTo(lessonA);
        assertThat(lessonResponse.getHasLocalChanges()).isFalse();
        assertThat(lessonResponse.getConfig().getCanEdit()).isTrue();
        assertThat(lessonResponse.getProfilesMap()).containsOnlyKeys(admin.getJid());

        LessonUpdateData updateData = new LessonUpdateData.Builder()
                .slug("lesson-a-new")
                .additionalNote("This is new lesson A")
                .build();

        lessonA = lessonClient.updateLesson(adminToken, lessonA.getJid(), updateData);
        assertThat(lessonA.getSlug()).isEqualTo("lesson-a-new");
        assertThat(lessonA.getAdditionalNote()).isEqualTo("This is new lesson A");

        assertThat(lessonClient.getLesson(adminToken, lessonA.getJid()).getData()).isEqualTo(lessonA);
        assertThat(lessonClient.getLessonBySlug(adminToken, "lesson-a-new")).isEqualTo(lessonA);
        assertNotFound(() -> lessonClient.getLessonBySlug(adminToken, "lesson-a"));

        String lessonAJid = lessonA.getJid();

        assertBadRequest(() -> lessonClient
                .updateLesson(adminToken, lessonAJid, new LessonUpdateData.Builder()
                        .from(updateData)
                        .slug("lesson-b")
                        .build()))
                .hasMessageContaining(SLUG_ALREADY_EXISTS);

        var response = lessonClient.getLessons(adminToken, new GetLessonsParams());
        assertThat(response.getData().getPage())
                .extracting(Lesson::getJid)
                .containsExactlyInAnyOrder(lessonA.getJid(), lessonB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(admin.getJid(), user.getJid());

        GetLessonsParams params = new GetLessonsParams();
        params.term = "lesson-a";
        response = lessonClient.getLessons(adminToken, params);
        assertThat(response.getData().getPage())
                .extracting(Lesson::getJid)
                .containsExactly(lessonA.getJid());

        // as user

        assertForbidden(() -> lessonClient
                .createLesson(userToken, new LessonCreateData.Builder()
                        .slug("lesson-c")
                        .additionalNote("")
                        .initialLanguage("en-US")
                        .build()));

        assertForbidden(() -> lessonClient.getLesson(userToken, lessonAJid));
        assertForbidden(() -> lessonClient.getLessonBySlug(userToken, "lesson-a-new"));
        assertForbidden(() -> lessonClient.updateLesson(userToken, lessonAJid, updateData));

        lessonResponse = lessonClient.getLesson(userToken, lessonB.getJid());
        assertThat(lessonResponse.getData().getSlug()).isEqualTo("lesson-b");
        assertThat(lessonResponse.getConfig().getCanEdit()).isTrue();

        assertThat(lessonClient.getLessonBySlug(userToken, "lesson-b").getJid()).isEqualTo(lessonB.getJid());

        response = lessonClient.getLessons(userToken, new GetLessonsParams());
        assertThat(response.getData().getPage())
                .extracting(Lesson::getJid)
                .containsExactly(lessonB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid());
    }
}
