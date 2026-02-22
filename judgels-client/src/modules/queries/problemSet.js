import { queryOptions } from '@tanstack/react-query';

import { problemSetAPI } from '../api/jerahmeel/problemSet';
import { problemSetProblemAPI } from '../api/jerahmeel/problemSetProblem';
import { getToken } from '../session';

export const problemSetsQueryOptions = params => {
  const { archiveSlug, name, page } = params || {};
  return queryOptions({
    queryKey: ['problem-sets', ...(params ? [params] : [])],
    queryFn: () => problemSetAPI.getProblemSets(getToken(), archiveSlug, name, page),
  });
};

export const problemSetBySlugQueryOptions = problemSetSlug =>
  queryOptions({
    queryKey: ['problem-set-by-slug', problemSetSlug],
    queryFn: () => problemSetAPI.getProblemSetBySlug(problemSetSlug),
  });

export const problemSetProblemQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias],
    queryFn: () => problemSetProblemAPI.getProblem(getToken(), problemSetJid, problemAlias),
  });

export const problemSetProblemsQueryOptions = problemSetJid =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problems'],
    queryFn: () => problemSetProblemAPI.getProblems(getToken(), problemSetJid),
  });

export const problemSetProblemWorksheetQueryOptions = (problemSetJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias, 'worksheet', ...(params ? [params] : [])],
    queryFn: () => problemSetProblemAPI.getProblemWorksheet(getToken(), problemSetJid, problemAlias, language),
  });
};

export const problemSetProblemReportQueryOptions = (problemSetJid, problemAlias) =>
  queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias, 'report'],
    queryFn: () => problemSetProblemAPI.getProblemReport(getToken(), problemSetJid, problemAlias),
  });

export const problemSetProblemEditorialQueryOptions = (problemSetJid, problemAlias, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem-set', problemSetJid, 'problem', problemAlias, 'editorial', ...(params ? [params] : [])],
    queryFn: () => problemSetProblemAPI.getProblemEditorial(problemSetJid, problemAlias, language),
  });
};
