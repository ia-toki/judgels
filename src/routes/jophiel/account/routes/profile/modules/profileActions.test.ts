import { profileActions } from './profileActions';
import { UserProfile } from '../../../../../../modules/api/jophiel/userProfile';
import { AppState } from '../../../../../../modules/store';
import { sessionState, token, userJid } from '../../../../../../fixtures/state';
import { PutProfile } from '../../../../../../modules/session/sessionReducer';

describe('profileActions', () => {
  let dispatch: jest.Mock<any>;

  const getState = (): Partial<AppState> => ({ session: sessionState });

  let userProfileAPI: jest.Mocked<any>;
  let toastActions: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    userProfileAPI = {
      getUserProfile: jest.fn(),
      updateUserProfile: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('fetch()', () => {
    const { fetch } = profileActions;
    const doFetch = async () => fetch()(dispatch, getState, { userProfileAPI });

    const profile: UserProfile = { name: 'First Last' };

    beforeEach(async () => {
      userProfileAPI.getUserProfile.mockImplementation(() => profile);

      await doFetch();
    });

    it('calls API to get user profile', () => {
      expect(userProfileAPI.getUserProfile).toHaveBeenCalledWith(token, userJid);
    });

    it('puts the profile', () => {
      expect(dispatch).toHaveBeenCalledWith(PutProfile.create(profile));
    });
  });

  describe('update()', () => {
    const { update } = profileActions;
    const doUpdate = async () => update(profile)(dispatch, getState, { userProfileAPI, toastActions });

    const profile: UserProfile = { name: 'First Last' };
    const newProfile: UserProfile = { name: 'Last First' };

    beforeEach(async () => {
      userProfileAPI.updateUserProfile.mockImplementation(() => newProfile);

      await doUpdate();
    });

    it('calls API to update user profile', () => {
      expect(userProfileAPI.updateUserProfile).toHaveBeenCalledWith(token, userJid, profile);
    });

    it('puts the new profile', () => {
      expect(dispatch).toHaveBeenCalledWith(PutProfile.create(newProfile));
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Profile updated.');
    });
  });
});
