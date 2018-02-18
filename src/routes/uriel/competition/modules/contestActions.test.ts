import { contestActions } from './contestActions';
import { ContestList } from '../../../../modules/api/uriel/contest';

describe('contestActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let contestAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    contestAPI = {
      getContests: jest.fn(),
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
});
