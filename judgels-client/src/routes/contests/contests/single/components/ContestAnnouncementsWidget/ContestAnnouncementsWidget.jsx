import { Tag } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../../../modules/api/uriel/contestWeb';
import { selectContest } from '../../../modules/contestSelectors';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';

import * as contestAnnouncementActions from '../../announcements/modules/contestAnnouncementActions';

class ContestAnnouncementsWidget extends Component {
  render() {
    if (this.props.announcementCount === 0) {
      return null;
    }
    return <Tag className="normal-weight">{this.props.announcementCount}</Tag>;
  }

  componentDidUpdate(prevProps) {
    if (this.props.announcementCount > prevProps.announcementCount) {
      // TODO(lungsin): change the notification tag to be more proper, e.g. using announcement JID.
      const timestamp = Math.floor(Date.now() / REFRESH_WEB_CONFIG_INTERVAL); // Use timestamp for notification tag
      const notificationTag = `announcement_${this.props.contestSlug}_timestamp_${timestamp}`;
      this.props.onAlertNewAnnouncements(notificationTag);
    }
  }
}

const mapStateToProps = state => ({
  contestSlug: selectContest(state).slug,
  announcementCount: selectContestWebConfig(state).announcementCount,
});
const mapDispatchToProps = {
  onAlertNewAnnouncements: contestAnnouncementActions.alertNewAnnouncements,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsWidget);
