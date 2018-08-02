import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';

import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import { contestAnnouncementActions as injectedContestAnnouncementActions } from '../../announcements/modules/contestAnnouncementActions';

interface ContestAnnouncementsWidgetProps {
  announcementsCount: number;
  onAlertNewAnnouncements: () => void;
}

class ContestAnnouncementsWidget extends React.Component<ContestAnnouncementsWidgetProps> {
  render() {
    if (this.props.announcementsCount === 0) {
      return null;
    }
    return <Tag>{this.props.announcementsCount}</Tag>;
  }

  componentDidUpdate(prevProps: ContestAnnouncementsWidgetProps) {
    if (this.props.announcementsCount > prevProps.announcementsCount) {
      this.props.onAlertNewAnnouncements();
    }
  }
}

function createContestAnnouncementsWidget(contestAnnouncementActions) {
  const mapStateToProps = (state: AppState) => ({
    announcementsCount: selectContestWebConfig(state)!.announcementsCount,
  });
  const mapDispatchToProps = {
    onAlertNewAnnouncements: contestAnnouncementActions.alertNewAnnouncements,
  };

  return connect(mapStateToProps, mapDispatchToProps)(ContestAnnouncementsWidget);
}

export default createContestAnnouncementsWidget(injectedContestAnnouncementActions);
