import nock from 'nock';

import { nockUriel } from '../../../../../../utils/nock';

import * as contestLogActions from './contestLogActions';

const contestJid = 'contest-jid';

describe('contestLogActions', () => {
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

      const response = await contestLogActions.getLogs(contestJid, username, problemAlias, page);
      expect(response).toEqual(responseBody);
    });
  });
});
