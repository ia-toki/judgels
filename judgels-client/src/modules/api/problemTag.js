import { get, put } from './http';
import { baseProblemURL, baseProblemsURL } from './problem';

export const problemTagAPI = {
  getTags: token => {
    return get(`${baseProblemsURL}/tags`, token);
  },

  getTopicTags: (token, problemJid) => {
    return get(`${baseProblemURL(problemJid)}/topic-tags`, token);
  },

  setTopicTags: (token, problemJid, topicTags) => {
    return put(`${baseProblemURL(problemJid)}/topic-tags`, token, topicTags);
  },
};
