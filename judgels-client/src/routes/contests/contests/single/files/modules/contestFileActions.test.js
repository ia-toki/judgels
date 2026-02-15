import nock from 'nock';

import { nockUriel } from '../../../../../../utils/nock';

import * as contestFileActions from './contestFileActions';

const contestJid = 'contestJid';

describe('contestFileActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getFiles()', () => {
    const responseBody = {
      data: [{ name: 'filehame' }],
    };

    it('calls API', async () => {
      nockUriel().get(`/contests/${contestJid}/files`).reply(200, responseBody);

      const response = await contestFileActions.getFiles(contestJid);
      expect(response).toEqual(responseBody);
    });
  });
});
