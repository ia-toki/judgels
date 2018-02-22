import { contestActions } from './contestActions';
import { PutContest } from './contestReducer';
import { ContestList } from '../../../../modules/api/uriel/contest';
import { contest, contestJid } from '../../../../fixtures/state';

describe('contestActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let contestAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    contestAPI = {
      getContests: jest.fn(),
      getContest: jest.fn(),
    };
  });

  describe('fetchList()', () => {
    const { fetchList } = contestActions;
    const doFetchList = async (page: number) => fetchList(page)(dispatch, getState, { contestAPI });

    beforeEach(async () => {
      const contestList: ContestList = {
        currentPage: 2,
        pageSize: 20,
        totalPages: 5,
        totalData: 3,
        data: [],
      };
      contestAPI.getContests.mockImplementation(() => contestList);

      await doFetchList(2);
    });

    it('calls API to get contest list', () => {
      expect(contestAPI.getContests).toHaveBeenCalledWith(2, 20);
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
      expect(contestAPI.getContest).toHaveBeenCalledWith(contestJid);
    });

    it('puts the contest', () => {
      expect(dispatch).toHaveBeenCalledWith(PutContest.create(contest));
    });
  });
});
