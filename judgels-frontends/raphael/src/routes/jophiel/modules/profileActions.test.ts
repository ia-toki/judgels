import { user } from 'fixtures/state';
import { AppState } from 'modules/store';
import { UsernamesMap } from 'modules/api/jophiel/user';

import { profileActions } from './profileActions';
import { PutUserJid } from './profileReducer';

describe('profileActions', () => {
  let dispatch: jest.Mock<any>;
  let userAPI: jest.Mocked<any>;
  const getState = (): Partial<AppState> => ({});

  beforeEach(() => {
    dispatch = jest.fn();

    userAPI = {
      translateUsernamesToJids: jest.fn(),
    };
  });

  describe('getUserJid()', () => {
    const { getUserJid } = profileActions;
    const doGetUserJid = async () => getUserJid(user.username)(dispatch, getState, { userAPI });

    describe('when user found', () => {
      beforeEach(async () => {
        const users: UsernamesMap = { [user.username]: user.jid };
        userAPI.translateUsernamesToJids.mockReturnValue(users);

        await doGetUserJid();
      });

      it('calls API to get user', () => {
        expect(userAPI.translateUsernamesToJids).toHaveBeenCalledWith([user.username]);
      });

      it('puts the user jid', () => {
        expect(dispatch).toHaveBeenCalledWith(PutUserJid.create(user.jid));
      });
    });

    describe('when user not found', () => {
      beforeEach(async () => {
        const users: UsernamesMap = {};
        userAPI.translateUsernamesToJids.mockReturnValue(users);
      });

      it('throws NotFoundError', async () => {
        await expect(doGetUserJid()).rejects.toMatchObject({});
      });
    });
  });
});
