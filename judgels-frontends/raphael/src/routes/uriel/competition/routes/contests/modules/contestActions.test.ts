import { contestActions } from './contestActions';
import { PutContest } from './contestReducer';
import { Contest, ContestPage } from '../../../../../../modules/api/uriel/contest';
import { contest, contestId, sessionState, token } from '../../../../../../fixtures/state';
import { AppState } from '../../../../../../modules/store';

describe('contestActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAPI = {
      getActiveContests: jest.fn(),
      getPastContests: jest.fn(),
      getContestById: jest.fn(),
      startVirtual: jest.fn(),
    };
  });

  describe('fetchActiveList()', () => {
    const { fetchActiveList } = contestActions;
    const doFetchActiveList = async () => fetchActiveList()(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestList: Contest[] = [contest];
      contestAPI.getActiveContests.mockImplementation(() => contestList);

      await doFetchActiveList();
    });

    it('calls API to get active contest list', () => {
      expect(contestAPI.getActiveContests).toHaveBeenCalledWith(token);
    });
  });

  describe('fetchPastPage()', () => {
    const { fetchPastPage } = contestActions;
    const doFetchPastPage = async () => fetchPastPage(2, 20)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestPage: ContestPage = {
        totalData: 3,
        data: [],
      };
      contestAPI.getPastContests.mockImplementation(() => contestPage);

      await doFetchPastPage();
    });

    it('calls API to get past contest list', () => {
      expect(contestAPI.getPastContests).toHaveBeenCalledWith(token, 2, 20);
    });
  });

  describe('fetchById()', () => {
    const { fetchById } = contestActions;
    const doFetch = async () => fetchById(contestId)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      contestAPI.getContestById.mockImplementation(() => contest);

      await doFetch();
    });

    it('calls API to get contest', () => {
      expect(contestAPI.getContestById).toHaveBeenCalledWith(token, contestId);
    });

    it('puts the contest', () => {
      expect(dispatch).toHaveBeenCalledWith(PutContest.create(contest));
    });
  });

  describe('startVirtual()', () => {
    const { startVirtual } = contestActions;
    const doStartVirtual = async () => startVirtual(contestId)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      await doStartVirtual();
    });

    it('calls API to start virtual contest', () => {
      expect(contestAPI.startVirtual).toHaveBeenCalledWith(token, contestId);
    });
  });
});
