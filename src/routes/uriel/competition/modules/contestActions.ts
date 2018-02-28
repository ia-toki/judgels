import { DelContest, PutContest } from './contestReducer';

export const contestActions = {
  fetchList: (page: number, pageSize: number) => {
    return async (dispatch, getState, { contestAPI }) => {
      return await contestAPI.getContests(page, pageSize);
    };
  },

  fetch: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const contest = await contestAPI.getContest(contestJid);
      dispatch(PutContest.create(contest));
      return contest;
    };
  },

  clear: DelContest.create,
};
