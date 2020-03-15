import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../modules/store';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import * as contestAnnouncementActions from '../../announcements/modules/contestAnnouncementActions';

interface ContestAnnouncementsWidgetProps {
  announcementCount: number;
  onAlertNewAnnouncements: () => void;
}

class ContestAnnouncementsWidget extends React.Component<ContestAnnouncementsWidgetProps> {
  render() {
    if (this.props.announcementCount === 0) {
      return null;
    }
    return <Tag className="normal-weight">{this.props.announcementCount}</Tag>;
  }

  componentDidUpdate(prevProps: ContestAnnouncementsWidgetProps) {
    if (this.props.announcementCount > prevProps.announcementCount) {
      this.props.onAlertNewAnnouncements();
    }
  }
}

const mapStateToProps = (state: AppState) => ({
  announcementCount: selectContestWebConfig(state).announcementCount,
});
const mapDispatchToProps = {
  onAlertNewAnnouncements: contestAnnouncementActions.alertNewAnnouncements,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsWidget);
