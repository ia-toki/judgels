import nock from 'nock';

import { nockJerahmeel } from '../../../../../../../../../utils/nock';

import * as chapterProblemActions from './chapterProblemActions';

const chapterJid = 'chapter-jid';
const problemAlias = 'problem-a';

describe('chapterProblemActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getProblemWorksheet()', () => {
    const language = 'id';
    const responseBody = {
      problem: {},
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/chapters/${chapterJid}/problems/${problemAlias}/worksheet`)
        .query({ language })
        .reply(200, responseBody);

      const response = await chapterProblemActions.getProblemWorksheet(chapterJid, problemAlias, language);
      expect(response).toEqual(responseBody);
    });
  });
});
