import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { JophielRole } from '../../../../modules/api/jophiel/role';
import { DelSession } from '../../../../modules/session/sessionReducer';
import { nockJophiel } from '../../../../utils/nock';
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
        nockJophiel()
          .post(`/session/logout`)
          .reply(200);

        await store.dispatch(logoutActions.logOut(path));

        expect(store.getActions()).toContainEqual(DelSession());
        expect(store.getActions()).toContainEqual(PutWebConfig({ role: { jophiel: JophielRole.Guest } }));
      });
    });

    describe('when the current token is already invalid', () => {
      it('ends the session anyway', async () => {
        nockJophiel()
          .post(`/session/logout`)
          .reply(401);

        await store.dispatch(logoutActions.logOut(path));

        expect(store.getActions()).toContainEqual(DelSession());
      });
    });

    describe('when logout is disabled', () => {
      it('does not log out', async () => {
        nockJophiel()
          .post(`/session/logout`)
          .reply(403, { message: 'Jophiel:LogoutDisabled' });

        await expect(store.dispatch(logoutActions.logOut(path))).rejects.toEqual(
          new Error('Logout is currently disabled.')
        );
      });
    });
  });
});
