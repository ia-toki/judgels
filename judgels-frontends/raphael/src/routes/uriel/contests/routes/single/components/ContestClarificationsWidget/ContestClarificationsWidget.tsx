import { Tag } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';

import { AppState } from '../../../../../../../modules/store';
import { selectContestWebConfig } from '../../../../modules/contestWebConfigSelectors';
import { contestClarificationActions as injectedContestClarificationsActions } from '../../routes/clarifications/modules/contestClarificationActions';

interface ContestClarificationsWidgetProps {
  answeredClarificationsCount: number;
  onAlertNewAnsweredClarifications: () => void;
}

class ContestClarificationsWidget extends React.Component<ContestClarificationsWidgetProps> {
  render() {
    if (this.props.answeredClarificationsCount === 0) {
      return null;
    }
    return <Tag>{this.props.answeredClarificationsCount}</Tag>;
  }

  componentDidUpdate(prevProps: ContestClarificationsWidgetProps) {
    if (this.props.answeredClarificationsCount > prevProps.answeredClarificationsCount) {
      this.props.onAlertNewAnsweredClarifications();
    }
  }
}

function createContestClarificationsWidget(contestClarificationActions) {
  const mapStateToProps = (state: AppState) => ({
    answeredClarificationsCount: selectContestWebConfig(state)!.answeredClarificationsCount,
  });
  const mapDispatchToProps = {
    onAlertNewAnsweredClarifications: contestClarificationActions.alertNewAnsweredClarifications,
  };

  return connect(mapStateToProps, mapDispatchToProps)(ContestClarificationsWidget);
}

export default createContestClarificationsWidget(injectedContestClarificationsActions);
