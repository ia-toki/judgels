import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import * as activateActions from './activateActions';

const emailCode = 'code';
const mockStore = configureMockStore([thunk]);

describe('activateActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('activateUser()', () => {
    it('calls API to activate user', async () => {
      nock(APP_CONFIG.apiUrls.jophiel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/user-account/activate/${emailCode}`)
        .reply(200)
        .post(`/user-account/activate/${emailCode}`)
        .reply(200);

      await store.dispatch(activateActions.activateUser(emailCode));
    });
  });
});
