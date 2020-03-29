import { get, post } from '../../api/http';
import { APP_CONFIG } from '../../../conf';

export interface Chapter {
  id: number;
  jid: string;
  name: string;
  description?: string;
}

export interface ChaptersResponse {
  data: Chapter[];
}

export interface ChapterCreateData {
  name: string;
}

export interface ChapterUpdateData {
  name?: string;
}

export interface ChapterInfo {
  name: string;
}

export interface ChapterProgress {
  solvedProblems: number;
  totalProblems: number;
}

export const baseChaptersURL = `${APP_CONFIG.apiUrls.jerahmeel}/chapters`;

export function baseChapterURL(chapterJid: string) {
  return `${baseChaptersURL}/${chapterJid}`;
}

export const chapterAPI = {
  createChapter: (token: string, data: ChapterCreateData): Promise<Chapter> => {
    return post(baseChaptersURL, token, data);
  },

  updateChapter: (token: string, chapterJid: string, data: ChapterUpdateData): Promise<Chapter> => {
    return post(`${baseChapterURL(chapterJid)}`, token, data);
  },

  getChapters: (token: string): Promise<ChaptersResponse> => {
    return get(baseChaptersURL, token);
  },
};
