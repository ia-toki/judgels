import { contestJid, sessionState, token } from 'fixtures/state';
import { ContestAnnouncement } from 'modules/api/uriel/contestAnnouncement';
import { AppState } from 'modules/store';

import { contestAnnouncementActions } from './contestAnnouncementActions';

describe('contestAnnouncementActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAnnouncementAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAnnouncementAPI = {
      getPublishedAnnouncements: jest.fn(),
    };
  });

  describe('getPublishedAnnouncements()', () => {
    const { getPublishedAnnouncements } = contestAnnouncementActions;
    const doGetPublishedAnnouncements = async () =>
      getPublishedAnnouncements(contestJid)(dispatch, getState, { contestAnnouncementAPI });

    beforeEach(async () => {
      const announcements = [] as ContestAnnouncement[];
      contestAnnouncementAPI.getPublishedAnnouncements.mockReturnValue(announcements);

      await doGetPublishedAnnouncements();
    });

    it('calls API to get published announcements', () => {
      expect(contestAnnouncementAPI.getPublishedAnnouncements).toHaveBeenCalledWith(token, contestJid);
    });
  });
});
