import { push } from 'connected-react-router';
import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import * as resetPasswordActions from './resetPasswordActions';

const emailCode = 'code123';
const newPassword = 'pass';
const mockStore = configureMockStore([thunk]);

describe('resetPasswordActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('resetPassword()', () => {
    describe('when the email code is valid', () => {
      it('succeeds', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/user-account/reset-password`)
          .reply(200)
          .post(`/user-account/reset-password`, { emailCode, newPassword })
          .reply(200);

        await store.dispatch(resetPasswordActions.resetPassword(emailCode, newPassword));

        expect(store.getActions()).toContainEqual(push('/login'));
      });
    });

    describe('when the email code is invalid', () => {
      it('throws a more descriptive error', async () => {
        nock(APP_CONFIG.apiUrls.jophiel)
          .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
          .options(`/user-account/reset-password`)
          .reply(200)
          .post(`/user-account/reset-password`, { emailCode, newPassword })
          .reply(400);

        await expect(store.dispatch(resetPasswordActions.resetPassword(emailCode, newPassword))).rejects.toEqual(
          new Error('Invalid code.')
        );
      });
    });
  });
});
