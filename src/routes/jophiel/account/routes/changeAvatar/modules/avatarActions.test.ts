import { AppState } from '../../../../../../modules/store';
import { sessionState, token, user, userJid } from '../../../../../../fixtures/state';
import { avatarActions } from './avatarActions';
import { User } from '../../../../../../modules/api/jophiel/user';
import { PutUser } from '../../../../../../modules/session/sessionReducer';
import { MAX_AVATAR_FILE_SIZE } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';

describe('avatarActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      getMyself: jest.fn(),
      updateUserAvatar: jest.fn(),
      deleteUserAvatar: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
      showErrorToast: jest.fn(),
    };
  });

  describe('change()', () => {
    const file = {} as File;
    const { change } = avatarActions;
    const doChange = async () => change(file)(dispatch, getState, { userAPI, toastActions });

    beforeEach(async () => {
      userAPI.getMyself.mockImplementation(() => Promise.resolve<User>(user));
      await doChange();
    });

    it('calls API to update avatar', () => {
      expect(userAPI.updateUserAvatar).toHaveBeenCalledWith(token, userJid, file);
    });

    it('updates the current user', () => {
      expect(userAPI.getMyself).toHaveBeenCalled();
      expect(dispatch).toHaveBeenCalledWith(PutUser.create(user));
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Avatar updated.');
    });
  });

  describe('remove()', () => {
    const { remove } = avatarActions;
    const doRemove = async () => remove()(dispatch, getState, { userAPI, toastActions });

    beforeEach(async () => {
      userAPI.getMyself.mockImplementation(() => Promise.resolve<User>(user));
      await doRemove();
    });

    it('calls API to delete avatar', () => {
      expect(userAPI.deleteUserAvatar).toHaveBeenCalledWith(token, userJid);
    });

    it('updates the current user', () => {
      expect(userAPI.getMyself).toHaveBeenCalled();
      expect(dispatch).toHaveBeenCalledWith(PutUser.create(user));
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Avatar removed.');
    });
  });

  describe('reject()', () => {
    const { reject } = avatarActions;
    const doReject = async (file: File) => reject(file)(dispatch, getState, { toastActions });

    it('rejects when the file size is too large', async () => {
      const file = { size: 2 * MAX_AVATAR_FILE_SIZE } as File;
      await doReject(file);
      expect(toastActions.showErrorToast).toHaveBeenCalled();
    });
  });
});
