import nock from 'nock';

import { nockJophiel } from '../../../../utils/nock';

import * as forgotPasswordActions from './forgotPasswordActions';

const email = 'email@domain.com';

describe('forgotPasswordActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('requestToResetPassword()', () => {
    it('calls API', async () => {
      nockJophiel().post(`/user-account/request-reset-password/${email}`).reply(200);

      await forgotPasswordActions.requestToResetPassword(email);
    });

    describe('when the email is not found', () => {
      it('throws with descriptive error', async () => {
        nockJophiel().post(`/user-account/request-reset-password/${email}`).reply(404);

        await expect(forgotPasswordActions.requestToResetPassword(email)).rejects.toEqual(
          new Error('Email not found.')
        );
      });
    });
  });
});
