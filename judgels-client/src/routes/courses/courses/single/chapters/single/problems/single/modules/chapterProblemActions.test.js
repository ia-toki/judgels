import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockJerahmeel } from '../../../../../../../../../utils/nock';

import * as chapterProblemActions from './chapterProblemActions';

const chapterJid = 'chapter-jid';
const problemAlias = 'problem-a';
const mockStore = configureMockStore([thunk]);

describe('chapterProblemActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function () {
    nock.cleanAll();
  });

  describe('getProblemWorksheet()', () => {
    const language = 'id';
    const responseBody = {
      problem: {},
    };

    it('calls API', async () => {
      nockJerahmeel()
        .get(`/chapters/${chapterJid}/problems/${problemAlias}/worksheet`)
        .query({ language })
        .reply(200, responseBody);

      const response = await store.dispatch(
        chapterProblemActions.getProblemWorksheet(chapterJid, problemAlias, language)
      );
      expect(response).toEqual(responseBody);
    });
  });
});
