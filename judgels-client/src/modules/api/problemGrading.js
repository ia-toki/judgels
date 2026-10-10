import { delete_, download, get, post, postMultipart, put } from './http';
import { baseProblemURL } from './problem';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/grading`;

export const problemGradingAPI = {
  updateGradingEngine: (token, problemJid, data) => {
    return put(`${baseURL(problemJid)}/engine`, token, data);
  },

  getGradingConfig: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/config`, token);
  },

  updateGradingConfig: (token, problemJid, data) => {
    return put(`${baseURL(problemJid)}/config`, token, data);
  },

  autoPopulateGradingConfig: (token, problemJid) => {
    return post(`${baseURL(problemJid)}/config/auto-populate`, token);
  },

  getGradingTestDataFiles: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/test-data`, token);
  },

  uploadGradingTestDataFile: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/test-data`, token, { file });
  },

  uploadGradingTestDataZip: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/test-data/zip`, token, { file });
  },

  downloadGradingTestDataFile: (token, problemJid, filename) => {
    return download(`${baseURL(problemJid)}/test-data/${encodeURIComponent(filename)}`, token, filename);
  },

  deleteGradingTestDataFile: (token, problemJid, filename) => {
    return delete_(`${baseURL(problemJid)}/test-data/${encodeURIComponent(filename)}`, token);
  },

  deleteGradingTestDataFiles: (token, problemJid) => {
    return delete_(`${baseURL(problemJid)}/test-data`, token);
  },

  getGradingHelperFiles: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/helpers`, token);
  },

  uploadGradingHelperFile: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/helpers`, token, { file });
  },

  uploadGradingHelperZip: (token, problemJid, file) => {
    return postMultipart(`${baseURL(problemJid)}/helpers/zip`, token, { file });
  },

  downloadGradingHelperFile: (token, problemJid, filename) => {
    return download(`${baseURL(problemJid)}/helpers/${encodeURIComponent(filename)}`, token, filename);
  },

  deleteGradingHelperFile: (token, problemJid, filename) => {
    return delete_(`${baseURL(problemJid)}/helpers/${encodeURIComponent(filename)}`, token);
  },

  deleteGradingHelperFiles: (token, problemJid) => {
    return delete_(`${baseURL(problemJid)}/helpers`, token);
  },

  getGradingLanguageRestriction: (token, problemJid) => {
    return get(`${baseURL(problemJid)}/language-restriction`, token);
  },

  updateGradingLanguageRestriction: (token, problemJid, languageRestriction) => {
    return put(`${baseURL(problemJid)}/language-restriction`, token, languageRestriction);
  },
};
