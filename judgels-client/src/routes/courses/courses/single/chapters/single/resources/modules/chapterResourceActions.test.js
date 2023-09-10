import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../../../../../utils/nock';
import * as chapterResourceActions from './chapterResourceActions';

const chapterJid = 'chapter-jid';
const mockStore = configureMockStore([thunk]);

describe('chapterResourceActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getResources()', () => {
    const responseBody = {
      data: [],
    };

    it('calls APIs', async () => {
      nockJerahmeel()
        .get(`/chapters/${chapterJid}/lessons`)
        .reply(200, responseBody);

      nockJerahmeel()
        .get(`/chapters/${chapterJid}/problems`)
        .reply(200, responseBody);

      const response = await store.dispatch(chapterResourceActions.getResources(chapterJid));
      expect(response).toEqual([responseBody, responseBody]);
    });
  });
});
