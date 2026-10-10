import { get } from './http';
import { baseProblemsURL } from './problem';

export const problemTagAPI = {
  getTags: token => {
    return get(`${baseProblemsURL}/tags`, token);
  },
};
