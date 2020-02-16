import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { JophielRole } from '../../../../modules/api/jophiel/role';
import { DelSession } from '../../../../modules/session/sessionReducer';
import { APP_CONFIG } from '../../../../conf';
import * as logoutActions from './logoutActions';
import { PutWebConfig } from '../../modules/userWebReducer';

const path = 'path';
const mockStore = configureMockStore([thunk]);

describe('logoutActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('logOut()', () => {
    describe('when the logout is successful', () => {
      it('succeeds', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/session/logout`)
          .reply(200)
          .post(`/session/logout`)
          .reply(200);

        await store.dispatch(logoutActions.logOut(path));

        expect(store.getActions()).toContainEqual(DelSession.create());
        expect(store.getActions()).toContainEqual(PutWebConfig.create({ role: JophielRole.Guest }));
      });
    });

    describe('when the current token is already invalid', () => {
      it('ends the session anyway', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/session/logout`)
          .reply(200)
          .post(`/session/logout`)
          .reply(401);

        await store.dispatch(logoutActions.logOut(path));

        expect(store.getActions()).toContainEqual(DelSession.create());
      });
    });
  });
});
