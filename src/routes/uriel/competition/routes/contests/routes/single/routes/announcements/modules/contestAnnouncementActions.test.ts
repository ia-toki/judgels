import { contestAnnouncementActions } from './contestAnnouncementActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import { ContestAnnouncement } from '../../../../../../../../../../modules/api/uriel/contestAnnouncement';
import { AppState } from '../../../../../../../../../../modules/store';

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

  describe('fetchPublishedList()', () => {
    const { fetchPublishedList } = contestAnnouncementActions;
    const doFetchPublishedList = async () =>
      fetchPublishedList(contestJid)(dispatch, getState, { contestAnnouncementAPI });

    beforeEach(async () => {
      const announcements = [] as ContestAnnouncement[];
      contestAnnouncementAPI.getPublishedAnnouncements.mockReturnValue(announcements);

      await doFetchPublishedList();
    });

    it('calls API to get contest announcements', () => {
      expect(contestAnnouncementAPI.getPublishedAnnouncements).toHaveBeenCalledWith(token, contestJid);
    });
  });
});
