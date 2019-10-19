import { get } from '../../../modules/api/http';

import { ChapterInfo } from './chapter';
import { baseCourseURL } from './course';

export interface CourseChapter {
  alias: string;
  chapterJid: string;
}

export interface CourseChaptersResponse {
  data: CourseChapter[];
  chaptersMap: { [chapterJid: string]: ChapterInfo };
}

const baseURL = (courseJid: string) => `${baseCourseURL(courseJid)}/chapters`;

export const courseChapterAPI = {
  getChapters: (token: string, courseJid: string): Promise<CourseChaptersResponse> => {
    return get(baseURL(courseJid), token);
  },
};
