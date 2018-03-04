import { roleActions } from './roleActions';
import { PutRole } from './roleReducer';
import { JophielRole } from '../../../modules/api/jophiel/user';
import { AppState } from '../../../modules/store';
import { sessionState, token, userJid } from '../../../fixtures/state';

describe('roleActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      getRole: jest.fn(),
    };
  });

  describe('get()', () => {
    const { get } = roleActions;
    const doGet = async () => get()(dispatch, getState, { userAPI });

    const role = JophielRole.Superadmin;

    beforeEach(async () => {
      userAPI.getRole.mockImplementation(() => role);

      await doGet();
    });

    it('calls API to get role', () => {
      expect(userAPI.getRole).toHaveBeenCalledWith(token, userJid);
    });

    it('puts the role', () => {
      expect(dispatch).toHaveBeenCalledWith(PutRole.create(role));
    });
  });
});
