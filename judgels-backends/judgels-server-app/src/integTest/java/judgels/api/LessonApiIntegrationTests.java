package judgels.api;

import static org.assertj.core.api.Assertions.assertThat;

import judgels.BaseJudgelsApiIntegrationTests;
import judgels.api.catalog.lesson.Lesson;
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
    void get_lessons() {
        Lesson lessonA = createLesson(adminToken, "lesson-a");
        Lesson lessonB = createLesson(userToken, "lesson-b");

        // as admin

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

        response = lessonClient.getLessons(userToken, new GetLessonsParams());
        assertThat(response.getData().getPage())
                .extracting(Lesson::getJid)
                .containsExactly(lessonB.getJid());
        assertThat(response.getProfilesMap()).containsOnlyKeys(user.getJid());
    }
}
