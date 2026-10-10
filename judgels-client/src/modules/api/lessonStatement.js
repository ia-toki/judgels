import { stringify } from 'query-string';

import { APP_CONFIG } from '../../conf';
import { delete_, download, get, post, postMultipart } from './http';
import { baseLessonURL } from './lesson';

const baseURL = lessonJid => `${baseLessonURL(lessonJid)}/statement`;

// Media files are written in a statement as `render/<filename>`.
export function formatStatementMediaUrls(text, lessonJid) {
  const renderURL = `${APP_CONFIG.apiUrl}/v2/lessons/${lessonJid}/render/`;
  return text.replace(/(src|href)="render\//g, `$1="${renderURL}`).replace(/url=render\//g, `url=${renderURL}`);
}

export const lessonStatementAPI = {
  getStatement: (token, lessonJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(lessonJid)}?${params}`, token);
  },

  updateStatement: (token, lessonJid, language, statement) => {
    const params = stringify({ language });
    return post(`${baseURL(lessonJid)}?${params}`, token, statement);
  },

  getStatementLanguages: (token, lessonJid) => {
    return get(`${baseURL(lessonJid)}/languages`, token);
  },

  addStatementLanguage: (token, lessonJid, language) => {
    return post(`${baseURL(lessonJid)}/languages/${language}`, token);
  },

  enableStatementLanguage: (token, lessonJid, language) => {
    return post(`${baseURL(lessonJid)}/languages/${language}/enable`, token);
  },

  disableStatementLanguage: (token, lessonJid, language) => {
    return post(`${baseURL(lessonJid)}/languages/${language}/disable`, token);
  },

  makeStatementLanguageDefault: (token, lessonJid, language) => {
    return post(`${baseURL(lessonJid)}/languages/${language}/make-default`, token);
  },

  getStatementMediaFiles: (token, lessonJid) => {
    return get(`${baseURL(lessonJid)}/media`, token);
  },

  uploadStatementMediaFile: (token, lessonJid, file) => {
    return postMultipart(`${baseURL(lessonJid)}/media`, token, { file });
  },

  uploadStatementMediaZip: (token, lessonJid, file) => {
    return postMultipart(`${baseURL(lessonJid)}/media/zip`, token, { file });
  },

  downloadStatementMediaFile: (token, lessonJid, filename) => {
    return download(`${baseURL(lessonJid)}/media/${encodeURIComponent(filename)}`, token, filename);
  },

  deleteStatementMediaFile: (token, lessonJid, filename) => {
    return delete_(`${baseURL(lessonJid)}/media/${encodeURIComponent(filename)}`, token);
  },

  deleteStatementMediaFiles: (token, lessonJid) => {
    return delete_(`${baseURL(lessonJid)}/media`, token);
  },
};
