import { DelContest, PutContest } from './contestReducer';

export const contestActions = {
  fetchList: (page: number) => {
    return async (dispatch, getState, { contestAPI }) => {
      return await contestAPI.getContests(page, 20);
    };
  },

  fetch: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const contest = await contestAPI.getContest(contestJid);
      dispatch(PutContest.create(contest));
      return contest;
    };
  },

  getScoreboard: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      return await contestAPI.getContestScoreboard(contestJid);
    };
  },

  clear: DelContest.create,
};
