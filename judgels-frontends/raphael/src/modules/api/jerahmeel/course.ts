import { get, post } from '../../api/http';
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
  totalSolvableChapters: number;
}

export interface CoursesResponse {
  data: Course[];
  curriculumDescription?: string;
  courseProgressesMap: { [courseJid: string]: CourseProgress };
}

export interface CourseCreateData {
  slug: string;
  name: string;
  description?: string;
}

export interface CourseUpdateData {
  slug?: string;
  name?: string;
  description?: string;
}

export enum CourseErrors {
  SlugAlreadyExists = 'Jerahmeel:CourseSlugAlreadyExists',
}

export const baseCoursesURL = `${APP_CONFIG.apiUrls.jerahmeel}/courses`;

export function baseCourseURL(courseJid: string) {
  return `${baseCoursesURL}/${courseJid}`;
}

export const courseAPI = {
  createCourse: (token: string, data: CourseCreateData): Promise<Course> => {
    return post(baseCoursesURL, token, data);
  },

  updateCourse: (token: string, courseJid: string, data: CourseUpdateData): Promise<Course> => {
    return post(`${baseCourseURL(courseJid)}`, token, data);
  },

  getCourses: (token: string): Promise<CoursesResponse> => {
    return get(`${baseCoursesURL}`, token);
  },

  getCourseBySlug: (token: string, courseSlug: string): Promise<Course> => {
    return get(`${baseCoursesURL}/slug/${courseSlug}`, token);
  },
};
