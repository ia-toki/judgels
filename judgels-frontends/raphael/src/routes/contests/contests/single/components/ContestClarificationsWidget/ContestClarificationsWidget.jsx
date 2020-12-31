import { Intent, Tag } from '@blueprintjs/core';
import { Component } from 'react';
import { connect } from 'react-redux';

import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { selectContest } from '../../../modules/contestSelectors';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import * as contestClarificationActions from '../../clarifications/modules/contestClarificationActions';
import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../../../modules/api/uriel/contestWeb';

class ContestClarificationsWidget extends Component {
  render() {
    if (this.props.clarificationCount === 0) {
      return null;
    }
    const intent = this.props.clarificationStatus === ContestClarificationStatus.Asked ? Intent.WARNING : Intent.NONE;
    return (
      <Tag className="normal-weight" intent={intent}>
        {this.props.clarificationCount}
      </Tag>
    );
  }

  componentDidUpdate(prevProps) {
    if (this.props.clarificationCount > prevProps.clarificationCount) {
      // TODO(lungsin): change the notification tag to be more proper, e.g. using clarification JID.
      const timestamp = Math.floor(Date.now() / REFRESH_WEB_CONFIG_INTERVAL); // Use timestamp for notification tag
      const notificationTag = `clarification_${this.props.contestSlug}_timestamp_${timestamp}`;
      this.props.onAlertNewClarifications(this.props.clarificationStatus, notificationTag);
    }
  }
}

const mapStateToProps = state => ({
  contestSlug: selectContest(state).slug,
  clarificationCount: selectContestWebConfig(state).clarificationCount,
  clarificationStatus: selectContestWebConfig(state).clarificationStatus,
});
const mapDispatchToProps = {
  onAlertNewClarifications: contestClarificationActions.alertNewClarifications,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsWidget);
