import { push } from 'react-router-redux';
import { SubmissionError } from 'redux-form';

import { BadRequestError } from 'modules/api/error';
import { ContestCreateData, ContestUpdateData, ContestErrors } from 'modules/api/uriel/contest';
import { selectToken } from 'modules/session/sessionSelectors';

import { DelContest, EditContest, PutContest } from './contestReducer';

export const contestActions = {
  createContest: (data: ContestCreateData) => {
    return async (dispatch, getState, { contestAPI, toastActions }) => {
      const token = selectToken(getState());
      try {
        await contestAPI.createContest(token, data);
      } catch (error) {
        if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
          throw new SubmissionError({ slug: 'Slug already exists' });
        }
        throw error;
      }
      dispatch(push(`/contests/${data.slug}`));
      dispatch(EditContest.create(true));
      toastActions.showSuccessToast('Contest created.');
    };
  },

  updateContest: (contestJid: string, contestSlug: string, data: ContestUpdateData) => {
    return async (dispatch, getState, { contestAPI, toastActions }) => {
      const token = selectToken(getState());
      try {
        await contestAPI.updateContest(token, contestJid, data);
      } catch (error) {
        if (error instanceof BadRequestError && error.message === ContestErrors.SlugAlreadyExists) {
          throw new SubmissionError({ slug: 'Slug already exists' });
        }
        throw error;
      }
      toastActions.showSuccessToast('Contest updated.');

      if (data.slug && data.slug !== contestSlug) {
        dispatch(push(`/contests/${data.slug}`));
      }
    };
  },

  getContests: (name?: string, page?: number) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getContests(token, name, page);
    };
  },

  getActiveContests: () => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getActiveContests(token);
    };
  },

  getContestBySlug: (contestSlug: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      const contest = await contestAPI.getContestBySlug(token, contestSlug);
      dispatch(PutContest.create(contest));
      return contest;
    };
  },

  startVirtualContest: (contestId: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      await contestAPI.startVirtualContest(token, contestId);
    };
  },

  resetVirtualContest: (contestId: string) => {
    return async (dispatch, getState, { contestAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestAPI.resetVirtualContest(token, contestId);
      toastActions.showSuccessToast('All contestant virtual start time has been reset.');
    };
  },

  getContestDescription: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      const { description } = await contestAPI.getContestDescription(token, contestJid);
      return description;
    };
  },

  updateContestDescription: (contestJid: string, description: string) => {
    return async (dispatch, getState, { contestAPI, toastActions }) => {
      const token = selectToken(getState());
      await contestAPI.updateContestDescription(token, contestJid, { description });
      toastActions.showSuccessToast('Description updated.');
    };
  },

  clearContest: DelContest.create,
};
