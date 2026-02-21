import { queryOptions } from '@tanstack/react-query';

import { BadRequestError } from '../api/error';
import { ContestErrors } from '../api/uriel/contest';
import { contestClarificationAPI } from '../api/uriel/contestClarification';
import { queryClient } from '../queryClient';
import { getToken } from '../session';

export const contestClarificationsQueryOptions = (contestJid, params) => {
  const { status, language, page } = params || {};
  return queryOptions({
    queryKey: ['contest', contestJid, 'clarifications', ...(params ? [params] : [])],
    queryFn: () => contestClarificationAPI.getClarifications(getToken(), contestJid, status, language, page),
  });
};

export const createContestClarificationMutationOptions = contestJid => ({
  mutationFn: data => contestClarificationAPI.createClarification(getToken(), contestJid, data),
  onSuccess: () => {
    queryClient.invalidateQueries(contestClarificationsQueryOptions(contestJid));
  },
});

export const answerContestClarificationMutationOptions = (contestJid, clarificationJid) => ({
  mutationFn: async answer => {
    try {
      await contestClarificationAPI.answerClarification(getToken(), contestJid, clarificationJid, { answer });
    } catch (error) {
      if (error instanceof BadRequestError && error.message === ContestErrors.ClarificationAlreadyAnswered) {
        throw new Error('This clarification has already been answered. Please refresh this page.');
      }
      throw error;
    }
  },
  onSuccess: () => {
    queryClient.invalidateQueries(contestClarificationsQueryOptions(contestJid));
  },
});
