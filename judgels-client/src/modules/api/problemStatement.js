import { stringify } from 'query-string';

import { APP_CONFIG } from '../../conf';
import { delete_, download, get, post, postMultipart } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/statement`;

// Media files are written in a statement as `render/<filename>`.
export function formatStatementMediaUrls(text, problemJid) {
  const renderURL = `${APP_CONFIG.apiUrl}/v2/problems/${problemJid}/render/`;
  return text.replace(/(src|href)="render\//g, `$1="${renderURL}`).replace(/url=render\//g, `url=${renderURL}`);
}

export const problemStatementAPI = {
  getStatement: (token, problemJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(problemJid)}?${params}`, token);
  },

  updateStatement: (token, problemJid, language, statement) => {
    const params = stringify({ language });
    return post(`${baseURL(problemJid)}?${params}`, token, statement);
  },

  getStatementLanguages: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/languages`, token);
  },

  addStatementLanguage: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}`, token);
  },

  enableStatementLanguage: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}/enable`, token);
  },

  disableStatementLanguage: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}/disable`, token);
  },

  makeStatementLanguageDefault: (token, problemJid, language) => {
    return post(`${baseURL(problemJid)}/languages/${language}/make-default`, token);
  },

  getStatementMediaFiles: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/media`, token);
  },

  uploadStatementMediaFile: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/media`, token, { file });
  },

  uploadStatementMediaZip: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/media/zip`, token, { file });
  },

  downloadStatementMediaFile: (token, problemJid, filename) => {
    return download(`${baseURL(problemJid)}/media/${encodeURIComponent(filename)}`, token, filename);
  },

  deleteStatementMediaFile: (token, problemJid, filename) => {
    return delete_(`${baseURL(problemJid)}/media/${encodeURIComponent(filename)}`, token);
  },

  deleteStatementMediaFiles: (token, problemJid) => {
    return delete_(`${baseURL(problemJid)}/media`, token);
  },
};
