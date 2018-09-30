import { contestJid, sessionState, token } from 'fixtures/state';
import {
  ContestAnnouncement,
  ContestAnnouncementData,
  ContestAnnouncementConfig,
  ContestAnnouncementStatus,
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
      getAllAnnouncements: jest.fn(),
      createAnnouncement: jest.fn(),
      getAnnouncementConfig: jest.fn(),
    };
    toastActions = {
      showSuccessToast: jest.fn(),
    };
  });

  describe('getAllAnnouncements()', () => {
    const { getAllAnnouncements } = contestAnnouncementActions;
    const doGetAllAnnouncements = async () =>
        getAllAnnouncements(contestJid)(dispatch, getState, { contestAnnouncementAPI });

    beforeEach(async () => {
      const announcements = [] as ContestAnnouncement[];
      contestAnnouncementAPI.getAllAnnouncements.mockReturnValue(announcements);

      await doGetAllAnnouncements();
    });

    it('calls API to get published announcements', () => {
      expect(contestAnnouncementAPI.getAllAnnouncements).toHaveBeenCalledWith(token, contestJid);
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

  describe('getAnnouncementConfig()', () => {
    const { getAnnouncementConfig } = contestAnnouncementActions;
    const doGetAnnouncementConfig = async () =>
      getAnnouncementConfig(contestJid)(dispatch, getState, { contestAnnouncementAPI });

    beforeEach(async () => {
      const response = {} as ContestAnnouncementConfig;
      contestAnnouncementAPI.getAnnouncementConfig.mockReturnValue(response);

      await doGetAnnouncementConfig();
    });

    it('calls API to get announcement config', () => {
      expect(contestAnnouncementAPI.getAnnouncementConfig).toHaveBeenCalledWith(token, contestJid);
    });
  });
});
