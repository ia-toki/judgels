import { get, put } from './http';
import { baseProblemsURL } from './problem';

const baseURL = `${baseProblemsURL}/writers`;

export const problemWriterAPI = {
  getWriters: token => {
    return get(baseURL, token);
  },

  setWriters: (token, data) => {
    return put(baseURL, token, data);
  },
};
