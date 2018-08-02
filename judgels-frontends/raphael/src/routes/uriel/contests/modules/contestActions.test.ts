import { ContestPage } from 'modules/api/uriel/contest';
import { AppState } from 'modules/store';
import { contest, contestId, sessionState, token } from 'fixtures/state';

import { contestActions } from './contestActions';
import { PutContest } from './contestReducer';

describe('contestActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAPI = {
      getActiveContests: jest.fn(),
      getContests: jest.fn(),
      getContestBySlug: jest.fn(),
      startVirtualContest: jest.fn(),
    };
  });

  describe('getContests()', () => {
    const { getContests } = contestActions;
    const doGetContests = async () => getContests(2, 20)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestPage: ContestPage = {
        totalData: 3,
        data: [],
      };
      contestAPI.getContests.mockImplementation(() => contestPage);

      await doGetContests();
    });

    it('calls API to get contests', () => {
      expect(contestAPI.getContests).toHaveBeenCalledWith(token, 2, 20);
    });
  });

  describe('getContestBySlug()', () => {
    const { getContestBySlug } = contestActions;
    const doGetContestBySlug = async () => getContestBySlug('ioi')(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      contestAPI.getContestBySlug.mockImplementation(() => contest);

      await doGetContestBySlug();
    });

    it('calls API to get contest', () => {
      expect(contestAPI.getContestBySlug).toHaveBeenCalledWith(token, 'ioi');
    });

    it('puts the contest', () => {
      expect(dispatch).toHaveBeenCalledWith(PutContest.create(contest));
    });
  });

  describe('startVirtualContest()', () => {
    const { startVirtualContest } = contestActions;
    const doStartVirtualContest = async () => startVirtualContest(contestId)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      await doStartVirtualContest();
    });

    it('calls API to start virtual contest', () => {
      expect(contestAPI.startVirtualContest).toHaveBeenCalledWith(token, contestId);
    });
  });
});
