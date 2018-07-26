import { profileActions } from './profileActions';
import { user } from '../../../fixtures/state';
import { AppState } from '../../../modules/store';
import { UsernamesMap } from '../../../modules/api/jophiel/user';
import { PutUserJid } from './profileReducer';

describe('profileActions', () => {
  let dispatch: jest.Mock<any>;
  let userAPI: jest.Mocked<any>;
  const getState = (): Partial<AppState> => ({});

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      getUsersByUsernames: jest.fn(),
    };
  });

  describe('getUserJid()', () => {
    const { getUserJid } = profileActions;
    const doGetUserJid = async () => getUserJid(user.username)(dispatch, getState, { userAPI });

    describe('when user found', () => {
      beforeEach(async () => {
        const users: UsernamesMap = { [user.username]: user };
        userAPI.getUsersByUsernames.mockReturnValue(users);

        await doGetUserJid();
      });

      it('calls API to get user', () => {
        expect(userAPI.getUsersByUsernames).toHaveBeenCalledWith([user.username]);
      });

      it('puts the user jid', () => {
        expect(dispatch).toHaveBeenCalledWith(PutUserJid.create(user.jid));
      });
    });

    describe('when user not found', () => {
      beforeEach(async () => {
        const users: UsernamesMap = {};
        userAPI.getUsersByUsernames.mockReturnValue(users);
      });

      it('throws NotFoundError', async () => {
        await expect(doGetUserJid()).rejects.toMatchObject({});
      });
    });
  });
});
