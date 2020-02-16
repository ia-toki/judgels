import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../conf';
import { OrderDir } from '../../../modules/api/pagination';
import * as userActions from './userActions';

const userJid = 'user-jid';
const mockStore = configureMockStore([thunk]);

describe('userActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getUser()', () => {
    const user = {
      name: 'User',
    };

    it('calls API to get users', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/users/${userJid}`)
        .reply(200, user);

      const response = await store.dispatch(userActions.getUser(userJid));
      expect(response).toEqual(user);
    });
  });

  describe('getUsers()', () => {
    const page = 1;
    const orderBy = 'username';
    const orderDir = OrderDir.ASC;
    const user = {
      name: 'User',
    };
    const users = {
      totalCount: 1,
      page: [user],
    };

    it('calls API to get users', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get('/users')
        .query({ page, orderBy, orderDir })
        .reply(200, users);

      const response = await store.dispatch(userActions.getUsers(page, orderBy, orderDir));
      expect(response).toEqual(users);
    });
  });
});
