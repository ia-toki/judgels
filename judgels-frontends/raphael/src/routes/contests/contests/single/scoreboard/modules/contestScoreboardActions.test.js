import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';
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

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/scoreboard`)
        .query({ frozen, showClosedProblems, page })
        .reply(200, responseBody);

      const response = await store.dispatch(
        contestScoreboardActions.getScoreboard(contestJid, frozen, showClosedProblems, page)
      );
      expect(response).toEqual(responseBody);
    });
  });

  describe('refreshScoreboard()', () => {
    it('calls API', async () => {
      nockUriel()
        .post(`/contests/${contestJid}/scoreboard/refresh`)
        .reply(200);

      await store.dispatch(contestScoreboardActions.refreshScoreboard(contestJid));
    });
  });
});
