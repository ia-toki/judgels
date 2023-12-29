import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { NotFoundError } from '../../../modules/api/error';
import { nockJophiel } from '../../../utils/nock';
import { PutUser } from './profileReducer';

import * as profileActions from './profileActions';

const userJid = 'user-jid';
const username = 'username';
const mockStore = configureMockStore([thunk]);

describe('profileActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getUser()', () => {
    describe('when user found', () => {
      it('calls API', async () => {
        nockJophiel().post(`/user-search/username-to-jid`).reply(200, { username: userJid });

        await store.dispatch(profileActions.getUser(username));
        expect(store.getActions()).toContainEqual(PutUser({ userJid, username }));
      });
    });

    describe('when user not found', () => {
      it('throws NotFoundError', async () => {
        nockJophiel().post(`/user-search/username-to-jid`).reply(200, {});

        await expect(store.dispatch(profileActions.getUser(username))).rejects.toBeInstanceOf(NotFoundError);
      });
    });
  });
});
