import { contestActions } from './contestActions';
import { PutContest } from './contestReducer';
import { Contest, ContestPage } from '../../../../../../modules/api/uriel/contest';
import { contest, contestJid, sessionState, token } from '../../../../../../fixtures/state';
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
      getContest: jest.fn(),
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

  describe('fetch()', () => {
    const { fetch } = contestActions;
    const doFetch = async () => fetch(contestJid)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      contestAPI.getContest.mockImplementation(() => contest);

      await doFetch();
    });

    it('calls API to get contest', () => {
      expect(contestAPI.getContest).toHaveBeenCalledWith(token, contestJid);
    });

    it('puts the contest', () => {
      expect(dispatch).toHaveBeenCalledWith(PutContest.create(contest));
    });
  });
});
