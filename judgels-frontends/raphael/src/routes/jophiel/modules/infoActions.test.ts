import { infoActions } from './infoActions';
import { UserInfo } from '../../../modules/api/jophiel/userInfo';
import { AppState } from '../../../modules/store';
import { sessionState, token, userJid } from '../../../fixtures/state';

describe('infoActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userInfoAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userInfoAPI = {
      getInfo: jest.fn(),
      updateInfo: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getInfo()', () => {
    const { getInfo } = infoActions;
    const doGetInfo = async () => getInfo(userJid)(dispatch, getState, { userInfoAPI });

    const info: UserInfo = { name: 'First Last' };

    beforeEach(async () => {
      userInfoAPI.getInfo.mockImplementation(() => info);

      await doGetInfo();
    });

    it('calls API to get user info', () => {
      expect(userInfoAPI.getInfo).toHaveBeenCalledWith(token, userJid);
    });
  });

  describe('updateInfo()', () => {
    const { updateInfo } = infoActions;
    const doUpdateInfo = async () => updateInfo(userJid, info)(dispatch, getState, { userInfoAPI, toastActions });

    const info: UserInfo = { name: 'First Last' };
    const newInfo: UserInfo = { name: 'Last First' };

    beforeEach(async () => {
      userInfoAPI.updateInfo.mockImplementation(() => newInfo);

      await doUpdateInfo();
    });

    it('calls API to update user info', () => {
      expect(userInfoAPI.updateInfo).toHaveBeenCalledWith(token, userJid, info);
    });

    it('shows success toast', () => {
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Info updated.');
    });
  });
});
