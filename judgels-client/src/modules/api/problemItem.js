import { stringify } from 'query-string';

import { delete_, get, post, put } from './http';
import { baseProblemURL } from './problem';
import { formatStatementMediaUrls } from './problemStatement';

const baseURL = problemJid => `${baseProblemURL(problemJid)}/items`;

// An item's statement and its choices are written like a problem statement, media files included.
export function formatItemMediaUrls(item, problemJid) {
  const { config } = item;
  return {
    ...item,
    config: {
      ...config,
      statement: formatStatementMediaUrls(config.statement, problemJid),
      ...(config.choices
        ? {
            choices: config.choices.map(choice => ({
              ...choice,
              content: formatStatementMediaUrls(choice.content, problemJid),
            })),
          }
        : {}),
    },
  };
}

export const problemItemAPI = {
  getItems: (token, problemJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(problemJid)}?${params}`, token);
  },

  createItem: (token, problemJid, data) => {
    return post(baseURL(problemJid), token, data);
  },

  getItem: (token, problemJid, itemJid, language) => {
    const params = stringify({ language });
    return get(`${baseURL(problemJid)}/${itemJid}?${params}`, token);
  },

  updateItem: (token, problemJid, itemJid, language, data) => {
    const params = stringify({ language });
    return put(`${baseURL(problemJid)}/${itemJid}?${params}`, token, data);
  },

  moveItemUp: (token, problemJid, itemJid) => {
    return post(`${baseURL(problemJid)}/${itemJid}/move-up`, token);
  },

  moveItemDown: (token, problemJid, itemJid) => {
    return post(`${baseURL(problemJid)}/${itemJid}/move-down`, token);
  },

  deleteItem: (token, problemJid, itemJid) => {
    return delete_(`${baseURL(problemJid)}/${itemJid}`, token);
  },
};
