import { push } from 'react-router-redux';
import { SubmissionError } from 'redux-form';

import { BadRequestError } from 'modules/api/error';
import { ContestData, ContestErrors } from 'modules/api/uriel/contest';
import { selectToken } from 'modules/session/sessionSelectors';

import { DelContest, EditContest, PutContest } from './contestReducer';

export const contestActions = {
  createContest: (data: ContestData) => {
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

  updateContest: (data: ContestData) => {
    return async (dispatch, getState, { contestAPI, toastActions }) => {
      return 0;
    };
  },

  getContests: (page: number, pageSize: number) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getContests(token, page, pageSize);
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

  getContestDescription: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getContestDescription(token, contestJid);
    };
  },

  getContestConfig: () => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getContestConfig(token);
    };
  },

  clearContest: DelContest.create,
};
