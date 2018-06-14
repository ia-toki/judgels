import { userActions } from './userActions';
import { AppState } from '../../../../../modules/store';
import { user } from '../../../../../fixtures/state';
import { sessionState, token } from '../../../../../fixtures/state';
import { Page } from '../../../../../modules/api/pagination';
import { User } from '../../../../../modules/api/jophiel/user';

describe('userActions', () => {
  let dispatch: jest.Mock<any>;
  let userAPI: jest.Mocked<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      getUsers: jest.fn(),
    };
  });

  describe('fetchList()', () => {
    const currentPage = 1;
    const { fetchList } = userActions;
    const doFetchList = async () => fetchList(currentPage)(dispatch, getState, { userAPI });

    beforeEach(async () => {
      const users: Page<User> = {
        totalData: 1,
        data: [user],
      };
      userAPI.getUsers.mockReturnValue(users);

      await doFetchList();
    });

    it('calls API to get user', () => {
      expect(userAPI.getUsers).toHaveBeenCalledWith(token, currentPage);
    });
  });
});
