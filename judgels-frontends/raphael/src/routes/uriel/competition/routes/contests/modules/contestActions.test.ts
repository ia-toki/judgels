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
      startVirtualContest: jest.fn(),
    };
  });

  describe('getActiveContests()', () => {
    const { getActiveContests } = contestActions;
    const doGetActiveContests = async () => getActiveContests()(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestList: Contest[] = [contest];
      contestAPI.getActiveContests.mockImplementation(() => contestList);

      await doGetActiveContests();
    });

    it('calls API to get active contests', () => {
      expect(contestAPI.getActiveContests).toHaveBeenCalledWith(token);
    });
  });

  describe('getPastContests()', () => {
    const { getPastContests } = contestActions;
    const doGetPastContests = async () => getPastContests(2, 20)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestPage: ContestPage = {
        totalData: 3,
        data: [],
      };
      contestAPI.getPastContests.mockImplementation(() => contestPage);

      await doGetPastContests();
    });

    it('calls API to get past contests', () => {
      expect(contestAPI.getPastContests).toHaveBeenCalledWith(token, 2, 20);
    });
  });

  describe('getContestById()', () => {
    const { getContestById } = contestActions;
    const doGetContestById = async () => getContestById(contestId)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      contestAPI.getContestById.mockImplementation(() => contest);

      await doGetContestById();
    });

    it('calls API to get contest', () => {
      expect(contestAPI.getContestById).toHaveBeenCalledWith(token, contestId);
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
