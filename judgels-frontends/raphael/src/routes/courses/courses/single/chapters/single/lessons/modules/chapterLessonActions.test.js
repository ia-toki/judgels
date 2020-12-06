import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../../../../../utils/nock';
import * as chapterLessonActions from './chapterLessonActions';

const chapterJid = 'chapter-jid';
const lessonAlias = 'lesson-a';
const mockStore = configureMockStore([thunk]);

describe('chapterLessonActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getLessons()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/chapters/${chapterJid}/lessons`)
        .reply(200, responseBody);

      const response = await store.dispatch(chapterLessonActions.getLessons(chapterJid));
      expect(response).toEqual(responseBody);
    });
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

      const response = await store.dispatch(chapterLessonActions.getLessonStatement(chapterJid, lessonAlias, language));
      expect(response).toEqual(responseBody);
    });
  });
});
