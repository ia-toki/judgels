import { queryOptions } from '@tanstack/react-query';

import { contestSubmissionBundleAPI } from '../api/uriel/contestSubmissionBundle';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestBundleLatestSubmissionsQueryOptions = (contestJid, problemAlias) => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'submissions', 'bundle', problemAlias],
    queryFn: () => contestSubmissionBundleAPI.getLatestSubmissions(getToken(), contestJid, problemAlias),
  });
};

export const createBundleItemSubmissionMutationOptions = (contestJid, problemAlias) => ({
  mutationFn: async ({ problemJid, itemJid, answer }) => {
    await contestSubmissionBundleAPI.createItemSubmission(getToken(), {
      containerJid: contestJid,
      problemJid,
      itemJid,
      answer,
    });
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestBundleLatestSubmissionsQueryOptions(contestJid, problemAlias));
  },
});
