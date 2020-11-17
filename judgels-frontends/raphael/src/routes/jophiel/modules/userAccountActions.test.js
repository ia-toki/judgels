import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../conf';

import * as userAccountActions from './userAccountActions';

const email = 'email@domain.com';
const mockStore = configureMockStore([thunk]);

describe('userAccountActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('resendActivationEmail()', () => {
    it('calls API to resend activation email', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/user-account/resend-activation-email/${email}`)
        .reply(200)
        .post(`/user-account/resend-activation-email/${email}`)
        .reply(200);

      await store.dispatch(userAccountActions.resendActivationEmail(email));
    });
  });
});
