import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ProblemErrors } from '../api/problem';
import { problemWriterAPI } from '../api/problemWriter';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemWritersQueryOptions = () =>
  queryOptions({
    queryKey: ['problems', 'writers'],
    queryFn: () => problemWriterAPI.getWriters(getToken()),
  });

export const setProblemWritersMutationOptions = () => ({
  mutationFn: async data => {
    try {
      return await problemWriterAPI.setWriters(getToken(), data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.WriterUsernamesNotFound) {
        throw new SubmissionError({ usernames: 'Users not found: ' + error.args.usernames });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemWritersQueryOptions());
  },
});
