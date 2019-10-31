import { selectToken } from '../../../../../../../../modules/session/sessionSelectors';

export const chapterLessonActions = {
  getLessons: (chapterJid: string) => {
    return async (dispatch, getState, { chapterLessonAPI }) => {
      const token = selectToken(getState());
      return await chapterLessonAPI.getLessons(token, chapterJid);
    };
  },

  getLessonStatement: (chapterJid: string, lessonAlias: string, language?: string) => {
    return async (dispatch, getState, { chapterLessonAPI }) => {
      const token = selectToken(getState());
      return await chapterLessonAPI.getLessonStatement(token, chapterJid, lessonAlias, language);
    };
  },
};
