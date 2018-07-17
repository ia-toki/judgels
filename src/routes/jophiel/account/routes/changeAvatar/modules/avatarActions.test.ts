import { AppState } from '../../../../../../modules/store';
import { sessionState, token, userJid } from '../../../../../../fixtures/state';
import { avatarActions } from './avatarActions';
import { MAX_AVATAR_FILE_SIZE } from '../../../../panels/avatar/ChangeAvatar/ChangeAvatar';

describe('avatarActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userAvatarAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userAvatarAPI = {
      updateAvatar: jest.fn(),
      deleteAvatar: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
      showErrorToast: jest.fn(),
    };
  });

  describe('change()', () => {
    const file = {} as File;
    const { change } = avatarActions;
    const doChange = async () => change(file)(dispatch, getState, { userAvatarAPI, toastActions });

    beforeEach(async () => {
      await doChange();
    });

    it('calls API to update avatar', () => {
      expect(userAvatarAPI.updateAvatar).toHaveBeenCalledWith(token, userJid, file);
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Avatar updated.');
    });
  });

  describe('remove()', () => {
    const { remove } = avatarActions;
    const doRemove = async () => remove()(dispatch, getState, { userAvatarAPI, toastActions });

    beforeEach(async () => {
      await doRemove();
    });

    it('calls API to delete avatar', () => {
      expect(userAvatarAPI.deleteAvatar).toHaveBeenCalledWith(token, userJid);
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
