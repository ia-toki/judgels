import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Card } from '../../../../../../../../../../components/Card/Card';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { AppState } from '../../../../../../../../../../modules/store';

interface ContestScoreboardPageProps extends RouteComponentProps<{ contestJid: string }> {
  contest?: Contest;
}

class ContestScoreboardPage extends React.Component<ContestScoreboardPageProps> {
  render() {
    if (!this.props.contest) {
      return null;
    }

    const { contest } = this.props;

    return <Card title={contest.name} />;
  }
}

function createContestScoreboardPage() {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: state.uriel.contest.value,
    } as Partial<ContestScoreboardPageProps>);
  return withRouter<any>(connect(mapStateToProps)(ContestScoreboardPage));
}

export default createContestScoreboardPage();
