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

  afterEach(function () {
    nock.cleanAll();
  });

  describe('createChapter()', () => {
    const params = { name: 'new-chapter' };

    it('calls API to create chapter', async () => {
      nock(APP_CONFIG.apiUrl)
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
      nock(APP_CONFIG.apiUrl)
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
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/chapters`)
        .reply(200, responseBody);

      const response = await store.dispatch(chapterActions.getChapters());
      expect(response).toEqual(responseBody);
    });
  });

  describe('getProblems()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get problems', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/chapters/${chapterJid}/problems`)
        .reply(200, responseBody);

      const response = await store.dispatch(chapterActions.getProblems(chapterJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('setProblems()', () => {
    const params = [];

    it('calls API to set problems', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/chapters/${chapterJid}/problems`)
        .reply(200)
        .put(`/chapters/${chapterJid}/problems`, params)
        .reply(200);

      await store.dispatch(chapterActions.setProblems(chapterJid, params));
    });
  });

  describe('getLessons()', () => {
    const responseBody = {
      data: [],
    };

    it('calls API to get lessons', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/chapters/${chapterJid}/lessons`)
        .reply(200, responseBody);

      const response = await store.dispatch(chapterActions.getLessons(chapterJid));
      expect(response).toEqual(responseBody);
    });
  });

  describe('setLessons()', () => {
    const params = [];

    it('calls API to set lessons', async () => {
      nock(APP_CONFIG.apiUrl)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/chapters/${chapterJid}/lessons`)
        .reply(200)
        .put(`/chapters/${chapterJid}/lessons`, params)
        .reply(200);

      await store.dispatch(chapterActions.setLessons(chapterJid, params));
    });
  });
});
