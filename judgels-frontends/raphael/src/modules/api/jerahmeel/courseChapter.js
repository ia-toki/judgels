import { get, put } from '../../../modules/api/http';

import { ChapterInfo, Chapter, ChapterProgress } from './chapter';
import { baseCourseURL } from './course';

export interface CourseChapter {
  alias: string;
  chapterJid: string;
}

export interface CourseChaptersResponse {
  data: CourseChapter[];
  chaptersMap: { [chapterJid: string]: ChapterInfo };
  chapterProgressesMap: { [chapterJid: string]: ChapterProgress };
}

const baseURL = (courseJid: string) => `${baseCourseURL(courseJid)}/chapters`;

export const courseChapterAPI = {
  getChapters: (token: string, courseJid: string): Promise<CourseChaptersResponse> => {
    return get(baseURL(courseJid), token);
  },

  setChapters: (token: string, courseJid: string, data: CourseChapter[]): Promise<void> => {
    return put(baseURL(courseJid), token, data);
  },

  getChapter: (token: string, courseJid: string, chapterAlias: string): Promise<Chapter> => {
    return get(`${baseURL(courseJid)}/${chapterAlias}`, token);
  },
};
