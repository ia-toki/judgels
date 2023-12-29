import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJophiel } from '../../../../utils/nock';

import * as activateActions from './activateActions';

const emailCode = 'code';
const mockStore = configureMockStore([thunk]);

describe('activateActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('activateUser()', () => {
    it('calls API', async () => {
      nockJophiel().post(`/user-account/activate/${emailCode}`).reply(200);

      await store.dispatch(activateActions.activateUser(emailCode));
    });
  });
});
