import { queryOptions } from '@tanstack/react-query';

import { problemItemAPI } from '../api/problemItem';
import { queryClient } from '../queryClient';
import { getToken } from '../session';
import { problemQueryOptions } from './problem';

// A write to a problem's files starts its local changes, which the problem itself reports,
// so each mutation invalidates the problem and, with it, everything inside it.
const invalidateProblem = problemJid => () => {
  queryClient.invalidateQueries(problemQueryOptions(problemJid));
};

export const problemItemsQueryOptions = (problemJid, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem', problemJid, 'items', ...(params ? [params] : [])],
    queryFn: () => problemItemAPI.getItems(getToken(), problemJid, language),
  });
};

export const createProblemItemMutationOptions = problemJid => ({
  mutationFn: type => problemItemAPI.createItem(getToken(), problemJid, { type }),
  onSuccess: invalidateProblem(problemJid),
});

export const problemItemQueryOptions = (problemJid, itemJid, params) => {
  const { language } = params || {};
  return queryOptions({
    queryKey: ['problem', problemJid, 'item', itemJid, ...(params ? [params] : [])],
    queryFn: () => problemItemAPI.getItem(getToken(), problemJid, itemJid, language),
  });
};

export const updateProblemItemMutationOptions = (problemJid, itemJid, language) => ({
  mutationFn: data => problemItemAPI.updateItem(getToken(), problemJid, itemJid, language, data),
  onSuccess: invalidateProblem(problemJid),
});

export const moveProblemItemUpMutationOptions = problemJid => ({
  mutationFn: itemJid => problemItemAPI.moveItemUp(getToken(), problemJid, itemJid),
  onSuccess: invalidateProblem(problemJid),
});

export const moveProblemItemDownMutationOptions = problemJid => ({
  mutationFn: itemJid => problemItemAPI.moveItemDown(getToken(), problemJid, itemJid),
  onSuccess: invalidateProblem(problemJid),
});

export const deleteProblemItemMutationOptions = problemJid => ({
  mutationFn: itemJid => problemItemAPI.deleteItem(getToken(), problemJid, itemJid),
  onSuccess: invalidateProblem(problemJid),
});
