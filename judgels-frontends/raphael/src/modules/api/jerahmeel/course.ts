import { get } from '../../api/http';
import { APP_CONFIG } from '../../../conf';

export interface Course {
  id: number;
  jid: string;
  slug: string;
  name: string;
  description?: string;
}

export interface CourseProgress {
  solvedChapters: number;
  totalChapters: number;
}

export interface CoursesResponse {
  data: Course[];
  curriculumDescription?: string;
  courseProgressesMap: { [courseJid: string]: CourseProgress };
}

export const baseCoursesURL = `${APP_CONFIG.apiUrls.jerahmeel}/courses`;

export function baseCourseURL(courseJid: string) {
  return `${baseCoursesURL}/${courseJid}`;
}

export const courseAPI = {
  getCourses: (): Promise<CoursesResponse> => {
    return get(`${baseCoursesURL}`);
  },

  getCourseBySlug: (courseSlug: string): Promise<Course> => {
    return get(`${baseCoursesURL}/slug/${courseSlug}`);
  },
};
