import { baseContestURL } from './contest';
import { get, postMultipart } from './http';

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
