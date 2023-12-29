import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';

import * as contestLogActions from './contestLogActions';

const contestJid = 'contest-jid';
const mockStore = configureMockStore([thunk]);

describe('contestLogActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getLogs()', () => {
    const username = 'username';
    const problemAlias = 'problemAlias';
    const page = 3;

    const responseBody = {
      data: {
        page: [{ id: 1 }],
      },
    };

    it('calls API to get logs', async () => {
      nockUriel().get(`/contests/${contestJid}/logs`).query({ username, problemAlias, page }).reply(200, responseBody);

      const response = await store.dispatch(contestLogActions.getLogs(contestJid, username, problemAlias, page));
      expect(response).toEqual(responseBody);
    });
  });
});
