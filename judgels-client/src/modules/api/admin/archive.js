import { APP_CONFIG } from '../../../conf';
import { get, post } from '../http';

const baseURL = `${APP_CONFIG.apiUrl}/admin/archives`;

export const adminArchiveAPI = {
  getArchives: token => {
    return get(baseURL, token);
  },

  getArchiveBySlug: (token, slug) => {
    return get(`${baseURL}/slug/${slug}`, token);
  },

  createArchive: (token, data) => {
    return post(baseURL, token, data);
  },

  updateArchive: (token, archiveJid, data) => {
    return post(`${baseURL}/${archiveJid}`, token, data);
  },
};
