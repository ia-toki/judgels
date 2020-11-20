import { get, postMultipart } from '../http';
import { baseContestURL } from './contest';

const baseURL = contestJid => `${baseContestURL(contestJid)}/files`;

export const contestFileAPI = {
  getFiles: (token, contestJid) => {
    return get(`${baseURL(contestJid)}`, token);
  },

  uploadFile: (token, contestJid, file) => {
    return postMultipart(`${baseURL(contestJid)}`, token, { file: file });
  },

  renderDownloadFilesUrl: contestJid => `${baseContestURL(contestJid)}/files`,

  renderDownloadFileUrl: (contestJid, filename) => `${baseContestURL(contestJid)}/files/${filename}`,
};
