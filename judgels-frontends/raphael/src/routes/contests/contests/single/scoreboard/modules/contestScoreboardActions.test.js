import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../conf';
import * as contestScoreboardActions from './contestScoreboardActions';

const contestJid = 'contest-jid';
const mockStore = configureMockStore([thunk]);

describe('contestScoreboardActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getScoreboard()', () => {
    const frozen = true;
    const showClosedProblems = false;
    const page = 1;
    const responseBody = {
      data: {},
    };

    it('calls API to get scoreboard', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/scoreboard`)
        .query({ frozen, showClosedProblems, page })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestScoreboardActions.getScoreboard(contestJid, frozen, showClosedProblems, page)
      );
      expect(response).toEqual(responseBody);
    });
  });
});
