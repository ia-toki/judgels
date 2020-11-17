import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { APP_CONFIG } from '../../../../../../conf';
import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import * as contestAnnouncementActions from './contestAnnouncementActions';

const contestJid = 'contest-jid';
const announcementJid = 'announcement-jid';
const mockStore = configureMockStore([thunk]);

describe('contestAnnouncementActions', () => {
  let store;

  beforeEach(() => {
    store = mockStore({});
  });

  afterEach(function() {
    nock.cleanAll();
  });

  describe('getAnnouncements()', () => {
    const page = 3;
    const responseBody = {
      data: [],
    };

    it('calls API to get announcements', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .get(`/contests/${contestJid}/announcements`)
        .query({ page })
        .reply(200, responseBody);

      const response = await store.dispatch(contestAnnouncementActions.getAnnouncements(contestJid, page));
      expect(response).toEqual(responseBody);
    });
  });

  describe('createAnnouncement()', () => {
    const params = {
      title: 'announcement title',
      content: 'announcement content',
      status: ContestAnnouncementStatus.Published,
    };

    it('calls API to create announcements', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/${contestJid}/announcements`)
        .reply(200)
        .post(`/contests/${contestJid}/announcements`, params)
        .reply(200);

      await store.dispatch(contestAnnouncementActions.createAnnouncement(contestJid, params));
    });
  });

  describe('updateAnnouncement()', () => {
    const params = {
      title: 'announcement title',
      content: 'announcement content',
      status: ContestAnnouncementStatus.Published,
    };

    it('calls API to update announcements', async () => {
      nock(APP_CONFIG.apiUrls.uriel)
        .defaultReplyHeaders({ 'access-control-allow-origin': '*' })
        .options(`/contests/${contestJid}/announcements/${announcementJid}`)
        .reply(200)
        .put(`/contests/${contestJid}/announcements/${announcementJid}`, params)
        .reply(200);

      await store.dispatch(contestAnnouncementActions.updateAnnouncement(contestJid, announcementJid, params));
    });
  });
});
