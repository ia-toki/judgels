import { get } from './http';
import { baseProblemsURL } from './trainingProblem';

const baseURL = `${baseProblemsURL}/tags`;

export const trainingProblemTagAPI = {
  getProblemTags: () => {
    return get(baseURL);
  },
};
