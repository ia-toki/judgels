import nock from 'nock';

import { nockJerahmeel } from '../../../../../../../../../utils/nock';

import * as chapterLessonActions from './chapterLessonActions';

const chapterJid = 'chapter-jid';
const lessonAlias = 'lesson-a';

describe('chapterLessonActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getLessonStatement()', () => {
    const language = 'id';
    const responseBody = {
      statement: {},
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/chapters/${chapterJid}/lessons/${lessonAlias}/statement`)
        .query({ language })
        .reply(200, responseBody);

      const response = await chapterLessonActions.getLessonStatement(chapterJid, lessonAlias, language);
      expect(response).toEqual(responseBody);
    });
  });
});
