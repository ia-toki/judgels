import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../modules/store';

import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import { contestAnnouncementActions as injectedContestAnnouncementActions } from '../../announcements/modules/contestAnnouncementActions';

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

function createContestAnnouncementsWidget(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    announcementCount: selectContestWebConfig(state)!.announcementCount,
  });
  const mapDispatchToProps = {
    onAlertNewAnnouncements: contestAnnouncementActions.alertNewAnnouncements,
  };

  return connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsWidget);
}

export default createContestAnnouncementsWidget(injectedContestAnnouncementActions);
