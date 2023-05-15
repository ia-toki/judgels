import nock from 'nock';
import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import { nockUriel } from '../../../../../../utils/nock';
import { ContestAnnouncementStatus } from '../../../../../../modules/api/uriel/contestAnnouncement';
import * as contestAnnouncementActions from './contestAnnouncementActions';

const contestJid = 'contestJid';
const announcementJid = 'announcementJid';
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
      data: {
        page: [{ jid: 'jid1' }],
      },
    };

    it('calls API', async () => {
      nockUriel()
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

    it('calls API', async () => {
      nockUriel()
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

    it('calls API', async () => {
      nockUriel()
        .options(`/contests/${contestJid}/announcements/${announcementJid}`)
        .reply(200)
        .put(`/contests/${contestJid}/announcements/${announcementJid}`, params)
        .reply(200);

      await store.dispatch(contestAnnouncementActions.updateAnnouncement(contestJid, announcementJid, params));
    });
  });
});
