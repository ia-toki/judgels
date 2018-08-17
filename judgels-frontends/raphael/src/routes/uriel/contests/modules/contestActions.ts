import { selectToken } from 'modules/session/sessionSelectors';

import { DelContest, PutContest } from './contestReducer';

export const contestActions = {
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
      const contestDescription = await contestAPI.getContestDescription(token, contestJid);
      dispatch(PutContest.create(contestDescription));
      return contestDescription;
    };
  },

  clearContest: DelContest.create,
};
