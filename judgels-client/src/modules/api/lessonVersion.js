import { delete_, get, post } from './http';
import { baseLessonURL } from './lesson';

const baseURL = lessonJid => `${baseLessonURL(lessonJid)}/versions`;

export const lessonVersionAPI = {
  getVersions: (token, lessonJid) => {
    return get(baseURL(lessonJid), token);
  },

  restoreVersion: (token, lessonJid, versionHash) => {
    return post(`${baseURL(lessonJid)}/${versionHash}/restore`, token);
  },

  commitVersionLocalChanges: (token, lessonJid, data) => {
    return post(`${baseURL(lessonJid)}/local/commit`, token, data);
  },

  rebaseVersionLocalChanges: (token, lessonJid) => {
    return post(`${baseURL(lessonJid)}/local/rebase`, token);
  },

  discardVersionLocalChanges: (token, lessonJid) => {
    return delete_(`${baseURL(lessonJid)}/local`, token);
  },
};
