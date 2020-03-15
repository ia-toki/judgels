import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../modules/store';
import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';

import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import * as contestClarificationActions from '../../clarifications/modules/contestClarificationActions';

interface ContestClarificationsWidgetProps {
  clarificationCount: number;
  clarificationStatus: ContestClarificationStatus;
  onAlertNewClarifications: (status: ContestClarificationStatus) => void;
}

class ContestClarificationsWidget extends React.Component<ContestClarificationsWidgetProps> {
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

  componentDidUpdate(prevProps: ContestClarificationsWidgetProps) {
    if (this.props.clarificationCount > prevProps.clarificationCount) {
      this.props.onAlertNewClarifications(this.props.clarificationStatus);
    }
  }
}

const mapStateToProps = (state: AppState) => ({
  clarificationCount: selectContestWebConfig(state).clarificationCount,
  clarificationStatus: selectContestWebConfig(state).clarificationStatus,
});
const mapDispatchToProps = {
  onAlertNewClarifications: contestClarificationActions.alertNewClarifications,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsWidget);
