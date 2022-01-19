import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../../../utils/nock';
import { PutCourseChapter } from './courseChapterReducer';
import * as courseChapterActions from './courseChapterActions';

const courseJid = 'course-jid';
const courseSlug = 'course-1';
const chapterAlias = 'A';
const mockStore = configureMockStore([thunk]);

describe('courseChapterActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getChapters()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/courses/${courseJid}/chapters`)
        .reply(200, responseBody);

      const response = await store.dispatch(courseChapterActions.getChapters(courseJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('getChapter()', () => {
    const responseBody = {
      jid: 'chapterJid',
      name: 'Chapter Name',
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/courses/${courseJid}/chapters/${chapterAlias}`)
        .reply(200, responseBody);

      const response = await store.dispatch(courseChapterActions.getChapter(courseJid, courseSlug, chapterAlias));
      expect(response).toEqual(responseBody);

      expect(store.getActions()).toContainEqual(
        PutCourseChapter({
          jid: 'chapterJid',
          name: 'Chapter Name',
          alias: chapterAlias,
          courseSlug: 'course-1',
        })
      );
    });
  });
});
