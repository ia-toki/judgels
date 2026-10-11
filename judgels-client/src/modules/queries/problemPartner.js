import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ProblemErrors } from '../api/problem';
import { problemPartnerAPI } from '../api/problemPartner';
import { SubmissionError } from '../form/submissionError';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const problemPartnersQueryOptions = problemJid =>
  queryOptions({
    queryKey: ['problem', problemJid, 'partners'],
    queryFn: () => problemPartnerAPI.getPartners(getToken(), problemJid),
  });

export const setProblemPartnersMutationOptions = problemJid => ({
  mutationFn: async data => {
    try {
      return await problemPartnerAPI.setPartners(getToken(), problemJid, data);
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ProblemErrors.PartnerUsernamesNotFound) {
        throw new SubmissionError({ csv: 'Users not found: ' + error.args.usernames });
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(problemPartnersQueryOptions(problemJid));
  },
});
