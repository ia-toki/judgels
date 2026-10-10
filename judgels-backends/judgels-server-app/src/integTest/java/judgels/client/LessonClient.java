package judgels.client;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.api.catalog.lesson.Lesson;
import judgels.api.catalog.lesson.LessonCreateData;
import judgels.api.catalog.lesson.LessonResponse;
import judgels.api.catalog.lesson.LessonUpdateData;
import judgels.api.catalog.lesson.LessonsResponse;

public interface LessonClient {
    class GetLessonsParams {
        public String term;
    }

    @RequestLine("GET /api/v4/lessons")
    @Headers("Authorization: Bearer {token}")
    LessonsResponse getLessons(@Param("token") String token, @QueryMap GetLessonsParams params);

    @RequestLine("POST /api/v4/lessons")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Lesson createLesson(@Param("token") String token, LessonCreateData data);

    @RequestLine("GET /api/v4/lessons/{lessonJid}")
    @Headers("Authorization: Bearer {token}")
    LessonResponse getLesson(@Param("token") String token, @Param("lessonJid") String lessonJid);

    @RequestLine("GET /api/v4/lessons/slug/{lessonSlug}")
    @Headers("Authorization: Bearer {token}")
    Lesson getLessonBySlug(@Param("token") String token, @Param("lessonSlug") String lessonSlug);

    @RequestLine("POST /api/v4/lessons/{lessonJid}")
    @Headers({"Authorization: Bearer {token}", "Content-Type: application/json"})
    Lesson updateLesson(
            @Param("token") String token,
            @Param("lessonJid") String lessonJid,
            LessonUpdateData data);
}
