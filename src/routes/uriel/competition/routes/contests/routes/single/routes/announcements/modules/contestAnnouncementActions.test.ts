import { contestAnnouncementActions } from './contestAnnouncementActions';
import { contestJid, sessionState, token } from '../../../../../../../../../../fixtures/state';
import { ContestAnnouncementsResponse } from '../../../../../../../../../../modules/api/uriel/contestAnnouncement';
import { AppState } from '../../../../../../../../../../modules/store';

describe('contestAnnouncementActions', () => {
  let dispatch: jest.Mock<any>;
  const getState = (): Partial<AppState> => ({ session: sessionState });

  let contestAnnouncementAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();

    contestAnnouncementAPI = {
      getAnnouncements: jest.fn(),
    };
  });

  describe('fetchList()', () => {
    const { fetchList } = contestAnnouncementActions;
    const doFetchList = async () => fetchList(contestJid)(dispatch, getState, { contestAnnouncementAPI });

    beforeEach(async () => {
      const announcements = {} as ContestAnnouncementsResponse;
      contestAnnouncementAPI.getAnnouncements.mockReturnValue(announcements);

      await doFetchList();
    });

    it('calls API to get contest announcements', () => {
      expect(contestAnnouncementAPI.getAnnouncements).toHaveBeenCalledWith(token, contestJid);
    });
  });
});
