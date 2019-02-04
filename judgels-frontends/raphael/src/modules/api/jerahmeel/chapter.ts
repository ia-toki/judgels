import { Page } from 'modules/api/pagination';
import { get } from 'modules/api/http';
// import { APP_CONFIG } from 'conf';

export interface Chapter {
  id: number;
  jid: string;
  name: string;
}

export interface ChaptersResponse {
  data: Page<Chapter>;
}

export const baseChapterURL = 'http://demo9804495.mockable.io/';
// export const baseChapterURL = `${APP_CONFIG.apiUrls.jerahmeel}/chapter`;

export const chapterAPI = {
  getChapters: (courseId: number): Promise<ChaptersResponse> => {
    return get(`${baseChapterURL}course/${courseId}/chapters`);
  },
};
