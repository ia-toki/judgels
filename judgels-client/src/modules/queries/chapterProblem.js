import { queryOptions } from '@tanstack/react-query';

import { chapterProblemAPI } from '../api/jerahmeel/chapterProblem';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const chapterProblemsQueryOptions = chapterJid =>
  queryOptions({
    queryKey: ['chapter', chapterJid, 'problems'],
    queryFn: () => chapterProblemAPI.getProblems(getToken(), chapterJid),
  });

export const chapterProblemWorksheetQueryOptions = (chapterJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['chapter', chapterJid, 'problem', problemAlias, 'worksheet', ...(params ? [params] : [])],
    queryFn: () => chapterProblemAPI.getProblemWorksheet(getToken(), chapterJid, problemAlias, language),
  });
};

export const setChapterProblemsMutationOptions = chapterJid => ({
  mutationFn: data => chapterProblemAPI.setProblems(getToken(), chapterJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(chapterProblemsQueryOptions(chapterJid));
  },
});
