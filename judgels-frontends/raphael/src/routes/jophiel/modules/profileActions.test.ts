import { profileActions } from './profileActions';
import { UserProfile } from '../../../modules/api/jophiel/userProfile';
import { AppState } from '../../../modules/store';
import { sessionState, token, userJid } from '../../../fixtures/state';

describe('profileActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userProfileAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userProfileAPI = {
      getProfile: jest.fn(),
      updateProfile: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getProfile()', () => {
    const { getProfile } = profileActions;
    const doGetProfile = async () => getProfile(userJid)(dispatch, getState, { userProfileAPI });

    const profile: UserProfile = { name: 'First Last' };

    beforeEach(async () => {
      userProfileAPI.getProfile.mockImplementation(() => profile);

      await doGetProfile();
    });

    it('calls API to get user profile', () => {
      expect(userProfileAPI.getProfile).toHaveBeenCalledWith(token, userJid);
    });
  });

  describe('updateProfile()', () => {
    const { updateProfile } = profileActions;
    const doUpdateProfile = async () =>
      updateProfile(userJid, profile)(dispatch, getState, { userProfileAPI, toastActions });

    const profile: UserProfile = { name: 'First Last' };
    const newProfile: UserProfile = { name: 'Last First' };

    beforeEach(async () => {
      userProfileAPI.updateProfile.mockImplementation(() => newProfile);

      await doUpdateProfile();
    });

    it('calls API to update user profile', () => {
      expect(userProfileAPI.updateProfile).toHaveBeenCalledWith(token, userJid, profile);
    });

    it('shows success toast', () => {
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Profile updated.');
    });
  });
});
