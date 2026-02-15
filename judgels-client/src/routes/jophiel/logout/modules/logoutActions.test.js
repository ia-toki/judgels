import nock from 'nock';

import { queryClient } from '../../../../modules/queryClient';
import { getToken, setSession } from '../../../../modules/session';
import { nockJophiel } from '../../../../utils/nock';

import * as logoutActions from './logoutActions';

describe('logoutActions', () => {
  beforeEach(() => {
    setSession('token123', {});
  });

  afterEach(function () {
    nock.cleanAll();
    queryClient.clear();
  });

  describe('logOut()', () => {
    describe('when the logout is successful', () => {
      it('succeeds', async () => {
        nockJophiel().post(`/session/logout`).reply(200);

        await logoutActions.logOut();

        expect(getToken()).toBeUndefined();
        expect(queryClient.getQueryData(['user-web-config'])).toEqual({ role: {} });
      });
    });

    describe('when the current token is already invalid', () => {
      it('ends the session anyway', async () => {
        nockJophiel().post(`/session/logout`).reply(401);

        await logoutActions.logOut();

        expect(getToken()).toBeUndefined();
      });
    });

    describe('when logout is disabled', () => {
      it('does not log out', async () => {
        nockJophiel().post(`/session/logout`).reply(403, { message: 'Jophiel:LogoutDisabled' });

        await expect(logoutActions.logOut()).rejects.toEqual(new Error('Logout is currently disabled.'));
      });
    });
  });
});
