import { queryOptions } from '@tanstack/react-query';

import { adminChapterProblemAPI } from '../api/admin/chapterProblem';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterProblemsQueryOptions = chapterJid =>
  queryOptions({
    queryKey: ['admin', 'chapter', chapterJid, 'problems'],
    queryFn: () => adminChapterProblemAPI.getProblems(getToken(), chapterJid),
  });

export const setChapterProblemsMutationOptions = chapterJid => ({
  mutationFn: data => adminChapterProblemAPI.setProblems(getToken(), chapterJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterProblemsQueryOptions(chapterJid));
  },
});
