import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../utils/nock';

import * as contestWebActions from './contestWebActions';

const contestJid = 'contestJid';
const mockStore = configureMockStore([thunk]);

describe('contestWebActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getContestByJidWithWebConfig()', () => {
    const responseBody = {
      contest: {},
      config: {},
    };

    it('calls API', async () => {
      nockUriel().get(`/contest-web/${contestJid}/with-config`).reply(200, responseBody);

      const response = await store.dispatch(contestWebActions.getContestByJidWithWebConfig(contestJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('getWebConfig()', () => {
    const responseBody = {
      canManage: true,
    };

    it('calls API', async () => {
      nockUriel().get(`/contest-web/${contestJid}/config`).reply(200, responseBody);

      await store.dispatch(contestWebActions.getWebConfig(contestJid));
    });
  });
});
