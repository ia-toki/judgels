import { queryOptions } from '@tanstack/react-query';

import { curriculumAPI } from '../api/curriculum';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const curriculumsQueryOptions = () =>
  queryOptions({
    queryKey: ['curriculums'],
    queryFn: () => curriculumAPI.getCurriculums(getToken()),
  });

export const curriculumByJidQueryOptions = curriculumJid =>
  queryOptions({
    queryKey: ['curriculum-by-jid', curriculumJid],
    queryFn: async () => {
      const response = await curriculumAPI.getCurriculums(getToken());
      return response.data.find(c => c.jid === curriculumJid);
    },
  });

export const updateCurriculumMutationOptions = curriculumJid => ({
  mutationFn: data => curriculumAPI.updateCurriculum(getToken(), curriculumJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(curriculumsQueryOptions());
    queryClient.invalidateQueries({ queryKey: ['curriculum-by-jid'] });
  },
});
