import * as contestAnnouncementActions from './contestAnnouncementActions';

describe('contestAnnouncementActions', () => {
  describe('alertNewAnnouncements()', () => {
    it('does not throw', async () => {
      await contestAnnouncementActions.alertNewAnnouncements('tag');
    });
  });
});
