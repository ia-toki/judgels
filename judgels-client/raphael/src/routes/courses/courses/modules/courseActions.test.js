import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../utils/nock';
import * as courseActions from './courseActions';
import { PutCourse } from './courseReducer';

const courseJid = 'course-jid';
const course = {
  id: 1,
  jid: courseJid,
  slug: 'competitive',
  name: 'Competitive',
};
const mockStore = configureMockStore([thunk]);

describe('courseActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getCourses()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/courses`)
        .reply(200, responseBody);

      const response = await store.dispatch(courseActions.getCourses());
      expect(response).toEqual(responseBody);
    });
  });

  describe('getCourseBySlug()', () => {
    it('calls API', async () => {
      nockJerahmeel()
        .get(`/courses/slug/competitive`)
        .reply(200, course);

      const response = await store.dispatch(courseActions.getCourseBySlug('competitive'));
      expect(response).toEqual(course);

      expect(store.getActions()).toContainEqual(PutCourse(course));
    });
  });
});
