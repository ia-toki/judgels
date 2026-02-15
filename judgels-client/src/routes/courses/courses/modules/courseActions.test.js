import nock from 'nock';

import { nockJerahmeel } from '../../../../utils/nock';

import * as courseActions from './courseActions';

describe('courseActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('getCourses()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API', async () => {
      nockJerahmeel().get(`/courses`).reply(200, responseBody);

      const response = await courseActions.getCourses();
      expect(response).toEqual(responseBody);
    });
  });
});
