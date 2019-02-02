import { Page } from 'modules/api/pagination';
import { get } from 'modules/api/http';
// import { APP_CONFIG } from 'conf';

export interface Course {
  id: number;
  jid: string;
  slug: string;
  name: string;
}

export interface CourseResponse {
  data: Page<Course>;
  // config: CourseConfig;
}

export const baseCourseURL = 'TODO';
// export const baseCourseURL = `${APP_CONFIG.apiUrls.jerahmeel}/course`;

export const courseAPI = {
  getCourse: (): Promise<CourseResponse> => {
    return get(`${baseCourseURL}`);
  },
};
