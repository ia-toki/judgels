import { queryOptions } from '@tanstack/react-query';

import { contestFileAPI } from '../api/uriel/contestFile';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestFilesQueryOptions = contestJid => {
  return queryOptions({
    queryKey: ['contest', contestJid, 'files'],
    queryFn: () => contestFileAPI.getFiles(getToken(), contestJid),
  });
};

export const uploadContestFileMutationOptions = contestJid => ({
  mutationFn: file => contestFileAPI.uploadFile(getToken(), contestJid, file),
  onSuccess: () => {
    queryClient.invalidateQueries(contestFilesQueryOptions(contestJid));
  },
});
