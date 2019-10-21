import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const chapterLessonActions = {
  getLessons: (chapterJid: string) => {
    return async (dispatch, getState, { chapterLessonAPI }) => {
      const token = selectToken(getState());
      return await chapterLessonAPI.getLessons(token, chapterJid);
    };
  },
};
