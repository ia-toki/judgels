import { Page } from 'modules/api/pagination';
import { get } from 'modules/api/http';
// import { APP_CONFIG } from 'conf';

export interface Course {
  id: number;
  jid: string;
  name: string;
}

export interface CoursesResponse {
  data: Page<Course>;
}

export const baseCourseURL = 'http://demo9804495.mockable.io/';
// export const baseCourseURL = `${APP_CONFIG.apiUrls.jerahmeel}/course`;

export const courseAPI = {
  getCourses: (): Promise<CoursesResponse> => {
    return get(`${baseCourseURL}courses`);
  },
  getCourseById: (courseId: number): Promise<Course> => {
    return get(`${baseCourseURL}course/${courseId}`);
  },
};
