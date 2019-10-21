import { APP_CONFIG } from '../../../conf';

export interface Chapter {
  id: number;
  jid: string;
  name: string;
  description?: string;
}

export interface ChapterInfo {
  name: string;
}

export const baseChaptersURL = `${APP_CONFIG.apiUrls.jerahmeel}/chapters`;

export function baseChapterURL(chapterJid: string) {
  return `${baseChaptersURL}/${chapterJid}`;
}

export const chapterAPI = {};
