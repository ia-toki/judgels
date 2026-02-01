import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../utils/nock';

import * as courseActions from './courseActions';

const mockStore = configureMockStore([thunk]);

describe('courseActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getCourses()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel().get(`/courses`).reply(200, responseBody);

      const response = await store.dispatch(courseActions.getCourses());
      expect(response).toEqual(responseBody);
    });
  });
});
