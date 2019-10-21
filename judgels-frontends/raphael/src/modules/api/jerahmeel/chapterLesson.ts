import { get } from '../../../modules/api/http';

import { LessonInfo } from '../sandalphon/lesson';
import { baseChapterURL } from './chapter';

export interface ChapterLesson {
  alias: string;
  lessonJid: string;
}

export interface ChapterLessonsResponse {
  data: ChapterLesson[];
  lessonsMap: { [lessonJid: string]: LessonInfo };
}

const baseURL = (chapterJid: string) => `${baseChapterURL(chapterJid)}/lessons`;

export const chapterLessonAPI = {
  getLessons: (token: string, chapterJid: string): Promise<ChapterLessonsResponse> => {
    return get(baseURL(chapterJid), token);
  },
};
