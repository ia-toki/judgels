export const chapterActions = {
  getChapters: (chapterId: number) => {
    return async (dispatch, getState, { chapterAPI }) => {
      return await chapterAPI.getChapters(chapterId);
    };
  },
};
