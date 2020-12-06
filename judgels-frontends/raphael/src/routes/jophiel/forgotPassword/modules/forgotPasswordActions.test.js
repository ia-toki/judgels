import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJophiel } from '../../../../utils/nock';
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
    it('calls API', async () => {
      nockJophiel()
        .post(`/user-account/request-reset-password/${email}`)
        .reply(200);

      await store.dispatch(forgotPasswordActions.requestToResetPassword(email));
    });

    describe('when the email is not found', () => {
      it('throws with descriptive error', async () => {
        nockJophiel()
          .post(`/user-account/request-reset-password/${email}`)
          .reply(404);

        await expect(store.dispatch(forgotPasswordActions.requestToResetPassword(email))).rejects.toEqual(
          new Error('Email not found.')
        );
      });
    });
  });
});
