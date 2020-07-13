import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../conf';
import * as contestLogActions from './contestLogActions';

const contestJid = 'contest-jid';
const mockStore = configureMockStore([thunk]);

describe('contestLogActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getLogs()', () => {
    const userJid = 'userJid';
    const problemJid = 'problemJid';
    const page = 3;

    const responseBody = {
      data: [],
    };

    it('calls API to get logs', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/logs`)
        .query({ userJid, problemJid, page })
        .reply(200, responseBody);

      const response = await store.dispatch(contestLogActions.getLogs(contestJid, userJid, problemJid, page));
      expect(response).toEqual(responseBody);
    });
  });
});
