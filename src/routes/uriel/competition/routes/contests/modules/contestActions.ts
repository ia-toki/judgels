import { DelContest, PutContest } from './contestReducer';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';

export const contestActions = {
  fetchList: (page: number, pageSize: number) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      return await contestAPI.getContests(token, page, pageSize);
    };
  },

  fetch: (contestJid: string) => {
    return async (dispatch, getState, { contestAPI }) => {
      const token = selectToken(getState());
      const contest = await contestAPI.getContest(token, contestJid);
      dispatch(PutContest.create(contest));
      return contest;
    };
  },

  clear: DelContest.create,
};
