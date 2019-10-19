import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export const courseChapterActions = {
  getChapters: (chapterJid: string) => {
    return async (dispatch, getState, { courseChapterAPI }) => {
      const token = selectToken(getState());
      return await courseChapterAPI.getChapters(token, chapterJid);
    };
  },
};
