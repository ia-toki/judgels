import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../conf';
import * as chapterActions from './chapterActions';

const chapterJid = 'chapter-jid';
const mockStore = configureMockStore([thunk]);

describe('chapterActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('createChapter()', () => {
    const params = { name: 'new-chapter' };

    it('calls API to create chapter', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/chapters`)
        .reply(200)
        .post(`/chapters`, params)
        .reply(200);

      await store.dispatch(chapterActions.createChapter(params));
    });
  });

  describe('updateChapter()', () => {
    const params = { name: 'New Name' };

    it('calls API to update chapter', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/chapters/${chapterJid}`)
        .reply(200)
        .post(`/chapters/${chapterJid}`, params)
        .reply(200);

      await store.dispatch(chapterActions.updateChapter(chapterJid, params));
    });
  });

  describe('getChapters()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get chapters', async () => {
      nock(APP_CONFIG.apiUrls.jerahmeel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/chapters`)
        .reply(200, responseBody);

      const response = await store.dispatch(chapterActions.getChapters());
      expect(response).toEqual(responseBody);
    });
  });
});
