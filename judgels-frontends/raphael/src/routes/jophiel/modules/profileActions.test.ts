import { user } from 'fixtures/state';
import { AppState } from 'modules/store';
import { NotFoundError } from 'modules/api/error';
import { UsernamesMap } from 'modules/api/jophiel/user';

import { profileActions } from './profileActions';
import { PutUserJid } from './profileReducer';

describe('profileActions', () => {
  let dispatch: jest.Mock<any>;
  let userSearchAPI: jest.Mocked<any>;
  const getState = (): Partial<AppState> => ({});

  beforeEach(() => {
    dispatch = jest.fn();

    userSearchAPI = {
      translateUsernamesToJids: jest.fn(),
    };
  });

  describe('getUserJid()', () => {
    const { getUserJid } = profileActions;
    const doGetUserJid = async () => getUserJid(user.username)(dispatch, getState, { userSearchAPI });

    describe('when user found', () => {
      beforeEach(async () => {
        const userJidsByUsername: UsernamesMap = { [user.username]: user.jid };
        userSearchAPI.translateUsernamesToJids.mockReturnValue(userJidsByUsername);

        await doGetUserJid();
      });

      it('calls API to get user', () => {
        expect(userSearchAPI.translateUsernamesToJids).toHaveBeenCalledWith([user.username]);
      });

      it('puts the user jid', () => {
        expect(dispatch).toHaveBeenCalledWith(PutUserJid.create(user.jid));
      });
    });

    describe('when user not found', () => {
      beforeEach(async () => {
        const users: UsernamesMap = {};
        userSearchAPI.translateUsernamesToJids.mockReturnValue(users);
      });

      it('throws NotFoundError', async () => {
        await expect(doGetUserJid()).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });
});
