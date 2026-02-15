import nock from 'nock';

import { nockJophiel } from '../../../../utils/nock';

import * as activateActions from './activateActions';

const emailCode = 'code';

describe('activateActions', () => {
  afterEach(function () {
    nock.cleanAll();
  });

  describe('activateUser()', () => {
    it('calls API', async () => {
      nockJophiel().post(`/user-account/activate/${emailCode}`).reply(200);

      await activateActions.activateUser(emailCode);
    });
  });
});
