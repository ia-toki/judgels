import { user } from '../../../fixtures/state';
import { AppState } from '../../../modules/store';
import { NotFoundError } from '../../../modules/api/error';
import { UsernamesMap } from '../../../modules/api/jophiel/user';

import { profileActions } from './profileActions';
import { PutUser } from './profileReducer';

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

  describe('getUser()', () => {
    const { getUser } = profileActions;
    const doGetUser = async () => getUser(user.username)(dispatch, getState, { userSearchAPI });

    describe('when user found', () => {
      beforeEach(async () => {
        const userJidsByUsername: UsernamesMap = { [user.username]: user.jid };
        userSearchAPI.translateUsernamesToJids.mockReturnValue(userJidsByUsername);

        await doGetUser();
      });

      it('calls API to get user', () => {
        expect(userSearchAPI.translateUsernamesToJids).toHaveBeenCalledWith([user.username]);
      });

      it('puts the user jid', () => {
        expect(dispatch).toHaveBeenCalledWith(PutUser.create({ userJid: user.jid, username: user.username }));
      });
    });

    describe('when user not found', () => {
      beforeEach(async () => {
        const users: UsernamesMap = {};
        userSearchAPI.translateUsernamesToJids.mockReturnValue(users);
      });

      it('throws NotFoundError', async () => {
        await expect(doGetUser()).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });
});
