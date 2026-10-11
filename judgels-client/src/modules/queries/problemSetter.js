import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ProblemErrors } from '../api/problem';
import { problemSetterAPI } from '../api/problemSetter';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemSettersQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'setters'],
    queryFn: () => problemSetterAPI.getSetters(getToken(), problemJid),
  });

export const setProblemSettersMutationOptions = problemJid => ({
  mutationFn: async setterUsernamesMap => {
    try {
      return await problemSetterAPI.setSetters(getToken(), problemJid, setterUsernamesMap);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.SetterUsernamesNotFound) {
        const usernamesNotFound = error.args.usernames.split(', ');
        const errors = {};
        Object.entries(setterUsernamesMap).forEach(([role, usernames]) => {
          const notFound = usernames.filter(username => usernamesNotFound.includes(username));
          if (notFound.length > 0) {
            errors[role] = 'Users not found: ' + notFound.join(', ');
          }
        });
        throw new SubmissionError(errors);
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemSettersQueryOptions(problemJid));
  },
});
