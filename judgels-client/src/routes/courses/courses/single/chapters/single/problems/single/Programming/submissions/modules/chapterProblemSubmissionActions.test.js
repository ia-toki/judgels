import nock from 'nock';

import { nockJerahmeel } from '../../../../../../../../../../../utils/nock';

import * as chapterProblemSubmissionActions from './chapterProblemSubmissionActions';

const chapterJid = 'chapter-jid';
const submissionId = 10;

describe('chapterProblemSubmissionActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getSubmissions()', () => {
    const username = 'username';
    const problemAlias = 'problemAlias';
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/submissions/programming`)
        .query({ containerJid: chapterJid, problemAlias, username, page })
        .reply(200, responseBody);

      const response = await chapterProblemSubmissionActions.getSubmissions(chapterJid, problemAlias, username, page);
      expect(response).toEqual(responseBody);
    });
  });

  describe('getSubmissionWithSource()', () => {
    const language = 'id';
    const responseBody = {
      data: {},
    };

    it('calls API', async () => {
      nockJerahmeel().get(`/submissions/programming/id/${submissionId}`).query({ language }).reply(200, responseBody);

      const response = await chapterProblemSubmissionActions.getSubmissionWithSource(submissionId, language);
      expect(response).toEqual(responseBody);
    });
  });
});
