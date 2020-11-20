import { Intent, Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { ContestClarificationStatus } from '../../../../../../modules/api/uriel/contestClarification';
import { selectContestWebConfig } from '../../../modules/contestWebConfigSelectors';
import * as contestClarificationActions from '../../clarifications/modules/contestClarificationActions';

class ContestClarificationsWidget extends React.Component {
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
      this.props.onAlertNewClarifications(this.props.clarificationStatus);
    }
  }
}

const mapStateToProps = state => ({
  clarificationCount: selectContestWebConfig(state).clarificationCount,
  clarificationStatus: selectContestWebConfig(state).clarificationStatus,
});
const mapDispatchToProps = {
  onAlertNewClarifications: contestClarificationActions.alertNewClarifications,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsWidget);
