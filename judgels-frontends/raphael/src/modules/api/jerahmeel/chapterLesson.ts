import { stringify } from 'query-string';

import { get } from '../../../modules/api/http';
import { LessonInfo, LessonStatement } from '../sandalphon/lesson';
import { baseChapterURL } from './chapter';

export interface ChapterLesson {
  alias: string;
  lessonJid: string;
}

export interface ChapterLessonsResponse {
  data: ChapterLesson[];
  lessonsMap: { [lessonJid: string]: LessonInfo };
}

export interface ChapterLessonStatement {
  defaultLanguage: string;
  languages: string[];
  lesson: ChapterLesson;
  statement: LessonStatement;
}

const baseURL = (chapterJid: string) => `${baseChapterURL(chapterJid)}/lessons`;

export const chapterLessonAPI = {
  getLessons: (token: string, chapterJid: string): Promise<ChapterLessonsResponse> => {
    return get(baseURL(chapterJid), token);
  },

  getLessonStatement: (
    token: string,
    chapterJid: string,
    lessonAlias: string,
    language?: string
  ): Promise<ChapterLessonStatement> => {
    const params = stringify({ language });
    return get(`${baseURL(chapterJid)}/${lessonAlias}/statement?${params}`, token);
  },
};
