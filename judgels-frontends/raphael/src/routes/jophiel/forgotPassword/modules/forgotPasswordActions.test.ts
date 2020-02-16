import { NotFoundError } from '../../../../modules/api/error';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import * as forgotPasswordActions from './forgotPasswordActions';

const email = 'email@domain.com';
const mockStore = configureMockStore([thunk]);

describe('forgotPasswordActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('requestToResetPassword()', () => {
    it('calls API to request to reset password', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/user-account/request-reset-password/${email}`)
        .reply(200)
        .post(`/user-account/request-reset-password/${email}`)
        .reply(200);

      await store.dispatch(forgotPasswordActions.requestToResetPassword(email));
    });

    describe('when the email is not found', () => {
      it('throws with descriptive error', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/user-account/request-reset-password/${email}`)
          .reply(200)
          .post(`/user-account/request-reset-password/${email}`)
          .reply(404);

        await expect(store.dispatch(forgotPasswordActions.requestToResetPassword(email))).rejects.toEqual(
          new Error('Email not found.')
        );
      });
    });
  });
});
