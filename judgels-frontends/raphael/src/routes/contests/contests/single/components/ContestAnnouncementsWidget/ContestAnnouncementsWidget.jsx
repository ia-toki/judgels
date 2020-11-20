import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import * as contestAnnouncementActions from '../../announcements/modules/contestAnnouncementActions';

class ContestAnnouncementsWidget extends React.Component {
  render() {
    if (this.props.announcementCount === 0) {
      return null;
    }
    return <Tag className="normal-weight">{this.props.announcementCount}</Tag>;
  }

  componentDidUpdate(prevProps) {
    if (this.props.announcementCount > prevProps.announcementCount) {
      this.props.onAlertNewAnnouncements();
    }
  }
}

const mapStateToProps = state => ({
  announcementCount: selectContestWebConfig(state).announcementCount,
});
const mapDispatchToProps = {
  onAlertNewAnnouncements: contestAnnouncementActions.alertNewAnnouncements,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsWidget);
