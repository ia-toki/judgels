import { contestActions } from './contestActions';
import { PutContest } from './contestReducer';
import { ContestList } from '../../../../../../modules/api/uriel/contest';
import { contest, contestJid, sessionState, token } from '../../../../../../fixtures/state';
import { AppState } from '../../../../../../modules/store';

describe('contestActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAPI = {
      getContests: jest.fn(),
      getContest: jest.fn(),
    };
  });

  describe('fetchList()', () => {
    const { fetchList } = contestActions;
    const doFetchList = async () => fetchList(2, 20)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestList: ContestList = {
        totalData: 3,
        data: [],
      };
      contestAPI.getContests.mockImplementation(() => contestList);

      await doFetchList();
    });

    it('calls API to get contest list', () => {
      expect(contestAPI.getContests).toHaveBeenCalledWith(token, 2, 20);
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
