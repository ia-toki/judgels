import nock from 'nock';
import { applyMiddleware, combineReducers, createStore } from 'redux';
import thunk from 'redux-thunk';

import sessionReducer, { PutUser } from '../../../../../modules/session/sessionReducer';
import { nockJophiel } from '../../../../../utils/nock';

import * as resetPasswordActions from './resetPasswordActions';

describe('resetPasswordActions', () => {
  let store;

  beforeEach(() => {
    store = createStore(
      combineReducers({
        session: sessionReducer,
      }),
      applyMiddleware(thunk)
    );
    store.dispatch(PutUser({ email: 'user@judgels.com' }));
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('requestToResetPassword()', () => {
    it('calls API', async () => {
      nockJophiel().post(`/user-account/request-reset-password/user@judgels.com`).reply(200);

      await store.dispatch(resetPasswordActions.requestToResetPassword());
    });
  });
});
