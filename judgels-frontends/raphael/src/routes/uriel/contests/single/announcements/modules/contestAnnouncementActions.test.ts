import { contestJid, sessionState, token, announcementJid } from 'fixtures/state';
import {
  ContestAnnouncement,
  ContestAnnouncementData,
  ContestAnnouncementConfig,
  ContestAnnouncementStatus,
  ContestAnnouncementsResponse,
} from 'modules/api/uriel/contestAnnouncement';
import { AppState } from 'modules/store';

import { contestAnnouncementActions } from './contestAnnouncementActions';

describe('contestAnnouncementActions', () => {
  let dispatch: jest.Mock<any>;
  let toastActions: jest.Mocked<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAnnouncementAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAnnouncementAPI = {
      getAnnouncements: jest.fn(),
      createAnnouncement: jest.fn(),
      getAnnouncementConfig: jest.fn(),
      updateAnnouncement: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getAnnouncements()', () => {
    const { getAnnouncements } = contestAnnouncementActions;
    const doGetAnnouncements = async () => getAnnouncements(contestJid)(dispatch, getState, { contestAnnouncementAPI });

    beforeEach(async () => {
      const data = [] as ContestAnnouncement[];
      const config = {
        isAllowedToCreateAnnouncement: true,
        isAllowedToEditAnnouncement: false,
      } as ContestAnnouncementConfig;
      const response = { data, config } as ContestAnnouncementsResponse;

      contestAnnouncementAPI.getAnnouncements.mockReturnValue(response);

      await doGetAnnouncements();
    });

    it('calls API to get published announcements', () => {
      expect(contestAnnouncementAPI.getAnnouncements).toHaveBeenCalledWith(token, contestJid);
    });
  });

  describe('createAnnouncement()', () => {
    const { createAnnouncement } = contestAnnouncementActions;
    const data = {
      title: 'announcement title',
      content: 'announcement content',
      status: ContestAnnouncementStatus.Published,
    } as ContestAnnouncementData;
    const doCreateAnnouncement = async () =>
      createAnnouncement(contestJid, data)(dispatch, getState, { contestAnnouncementAPI, toastActions });

    beforeEach(async () => {
      await doCreateAnnouncement();
    });

    it('calls API to create announcements', () => {
      expect(contestAnnouncementAPI.createAnnouncement).toHaveBeenCalledWith(token, contestJid, data);
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Announcement created.');
    });
  });

  describe('updateAnnouncement()', () => {
    const { updateAnnouncement } = contestAnnouncementActions;
    const data = {
      title: 'announcement title',
      content: 'announcement content',
      status: ContestAnnouncementStatus.Published,
    } as ContestAnnouncementData;
    const doUpdateAnnouncement = async () =>
      updateAnnouncement(contestJid, announcementJid, data)(dispatch, getState, {
        contestAnnouncementAPI,
        toastActions,
      });

    beforeEach(async () => {
      await doUpdateAnnouncement();
    });

    it('calls API to update announcements', () => {
      expect(contestAnnouncementAPI.updateAnnouncement).toHaveBeenCalledWith(token, contestJid, announcementJid, data);
      expect(toastActions.showSuccessToast).toHaveBeenCalledWith('Announcement edited.');
    });
  });
});
