import { stringify } from 'query-string';

import { APP_CONFIG } from '../../conf';
import { delete_, download, get, post, postMultipart, put } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/editorial`;

// Media files are written in an editorial as `render/<filename>`.
export function formatEditorialMediaUrls(text, problemJid) {
  const renderURL = `${APP_CONFIG.apiUrl}/v2/problems/${problemJid}/editorials/render/`;
  return text.replace(/(src|href)="render\//g, `$1="${renderURL}`).replace(/url=render\//g, `url=${renderURL}`);
}

export const problemEditorialAPI = {
  getEditorial: (token, problemJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(problemJid)}?${params}`, token);
  },

  createEditorial: (token, problemJid, data) => {
    return post(baseURL(problemJid), token, data);
  },

  updateEditorial: (token, problemJid, language, editorial) => {
    const params = stringify({ language });
    return put(`${baseURL(problemJid)}?${params}`, token, editorial);
  },

  getEditorialLanguages: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/languages`, token);
  },

  addEditorialLanguage: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}`, token);
  },

  enableEditorialLanguage: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}/enable`, token);
  },

  disableEditorialLanguage: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}/disable`, token);
  },

  makeEditorialLanguageDefault: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}/make-default`, token);
  },

  getEditorialMediaFiles: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/media`, token);
  },

  uploadEditorialMediaFile: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/media`, token, { file });
  },

  uploadEditorialMediaZip: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/media/zip`, token, { file });
  },

  downloadEditorialMediaFile: (token, problemJid, filename) => {
    return download(`${baseURL(problemJid)}/media/${encodeURIComponent(filename)}`, token, filename);
  },

  deleteEditorialMediaFile: (token, problemJid, filename) => {
    return delete_(`${baseURL(problemJid)}/media/${encodeURIComponent(filename)}`, token);
  },

  deleteEditorialMediaFiles: (token, problemJid) => {
    return delete_(`${baseURL(problemJid)}/media`, token);
  },
};
