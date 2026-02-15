import nock from 'nock';

import { setSession } from '../../../../../modules/session';
import { nockJophiel } from '../../../../../utils/nock';

import * as resetPasswordActions from './resetPasswordActions';

describe('resetPasswordActions', () => {
  beforeEach(() => {
    setSession(undefined, { email: 'user@judgels.com' });
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('requestToResetPassword()', () => {
    it('calls API', async () => {
      nockJophiel().post(`/user-account/request-reset-password/user@judgels.com`).reply(200);

      await resetPasswordActions.requestToResetPassword();
    });
  });
});
