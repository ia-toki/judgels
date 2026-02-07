import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../../../utils/nock';

import * as courseChapterActions from './courseChapterActions';

const courseJid = 'course-jid';
const mockStore = configureMockStore([thunk]);

describe('courseChapterActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getChapters()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel().get(`/courses/${courseJid}/chapters`).reply(200, responseBody);

      const response = await store.dispatch(courseChapterActions.getChapters(courseJid));
      expect(response).toEqual(responseBody);
    });
  });
});
