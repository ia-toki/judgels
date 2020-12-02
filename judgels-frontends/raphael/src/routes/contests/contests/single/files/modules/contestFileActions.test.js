import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';
import * as contestFileActions from './contestFileActions';

const contestJid = 'contestJid';
const mockStore = configureMockStore([thunk]);

describe('contestFileActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getFiles()', () => {
    const responseBody = {
      data: [{ name: 'filehame' }],
    };

    it('calls API', async () => {
      nockUriel()
        .get(`/contests/${contestJid}/files`)
        .reply(200, responseBody);

      const response = await store.dispatch(contestFileActions.getFiles(contestJid));
      expect(response).toEqual(responseBody);
    });
  });
});
