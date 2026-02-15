import nock from 'nock';

import { nockJophiel } from '../../../utils/nock';

import * as userAccountActions from './userAccountActions';

const email = 'email@domain.com';

describe('userAccountActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('resendActivationEmail()', () => {
    it('calls API', async () => {
      nockJophiel().post(`/user-account/resend-activation-email/${email}`).reply(200);

      await userAccountActions.resendActivationEmail(email);
    });
  });
});
