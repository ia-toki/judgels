import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJophiel } from '../../../utils/nock';

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
    it('calls API', async () => {
      nockJophiel()
        .post(`/user-account/resend-activation-email/${email}`)
        .reply(200);

      await store.dispatch(userAccountActions.resendActivationEmail(email));
    });
  });
});
