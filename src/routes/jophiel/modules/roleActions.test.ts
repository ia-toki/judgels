import { roleActions } from './roleActions';
import { PutRole } from './roleReducer';
import { JophielRole } from '../../../modules/api/jophiel/my';
import { AppState } from '../../../modules/store';
import { sessionState, token } from '../../../fixtures/state';

describe('roleActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let myAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    myAPI = {
      getMyRole: jest.fn(),
    };
  });

  describe('get()', () => {
    const { get } = roleActions;
    const doGet = async () => get()(dispatch, getState, { myAPI });

    const role = JophielRole.Superadmin;

    beforeEach(async () => {
      myAPI.getMyRole.mockImplementation(() => role);

      await doGet();
    });

    it('calls API to get role', () => {
      expect(myAPI.getMyRole).toHaveBeenCalledWith(token);
    });

    it('puts the role', () => {
      expect(dispatch).toHaveBeenCalledWith(PutRole.create(role));
    });
  });
});
