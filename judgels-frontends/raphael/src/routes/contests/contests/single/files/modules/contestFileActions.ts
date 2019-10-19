import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export const contestFileActions = {
  getFiles: (contestJid: string) => {
    return async (dispatch, getState, { contestFileAPI }) => {
      const token = selectToken(getState());
      return await contestFileAPI.getFiles(token, contestJid);
    };
  },

  uploadFile: (contestJid: string, file: File) => {
    return async (dispatch, getState, { contestFileAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestFileAPI.uploadFile(token, contestJid, file);

      toastActions.showSuccessToast('File uploaded.');
    };
  },
};
