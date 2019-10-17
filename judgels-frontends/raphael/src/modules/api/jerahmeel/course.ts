import { get } from '../../api/http';
import { APP_CONFIG } from '../../../conf';

export interface Course {
  id: number;
  jid: string;
  name: string;
}

export interface CoursesResponse {
  data: Course[];
}

export const baseCourseURL = `${APP_CONFIG.apiUrls.jerahmeel}/courses`;

export const courseAPI = {
  getCourses: (): Promise<CoursesResponse> => {
    return get(`${baseCourseURL}`);
  },
};
