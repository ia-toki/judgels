import nock from 'nock';

import { nockJerahmeel } from '../../../../../../../../utils/nock';

import * as chapterResourceActions from './chapterResourceActions';

const chapterJid = 'chapter-jid';

describe('chapterResourceActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getResources()', () => {
    const responseBody = {
      data: [],
    };

    it('calls APIs', async () => {
      nockJerahmeel().get(`/chapters/${chapterJid}/lessons`).reply(200, responseBody);

      nockJerahmeel().get(`/chapters/${chapterJid}/problems`).reply(200, responseBody);

      const response = await chapterResourceActions.getResources(chapterJid);
      expect(response).toEqual([responseBody, responseBody]);
    });
  });
});
