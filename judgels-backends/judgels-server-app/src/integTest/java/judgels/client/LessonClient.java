package judgels.client;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;
import judgels.api.catalog.lesson.LessonsResponse;

public interface LessonClient {
    class GetLessonsParams {
        public String term;
    }

    @RequestLine("GET /api/v4/lessons")
    @Headers("Authorization: Bearer {token}")
    LessonsResponse getLessons(@Param("token") String token, @QueryMap GetLessonsParams params);
}
