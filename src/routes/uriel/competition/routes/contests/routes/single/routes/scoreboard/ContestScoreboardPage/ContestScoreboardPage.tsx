import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { Card } from '../../../../../../../../../../components/Card/Card';
import { Contest } from '../../../../../../../../../../modules/api/uriel/contest';
import { singleContestActions as injectedSingleContestActions } from '../../../modules/singleContestActions';

interface ContestScoreboardPageProps extends RouteComponentProps<{ contestJid: string }> {
  onGetContest: (contestJid: string) => Promise<Contest>;
}

interface ContestScoreboardPageState {
  contest?: Contest;
}

class ContestScoreboardPage extends React.Component<ContestScoreboardPageProps, ContestScoreboardPageState> {
  state: ContestScoreboardPageState = {};

  async componentDidMount() {
    const contest = await this.props.onGetContest(this.props.match.params.contestJid);
    this.setState({ contest });
  }

  render() {
    if (!this.state.contest) {
      return null;
    }

    const { contest } = this.state;

    return <Card title={contest.name} />;
  }
}

function createContestScoreboardPage(singleContestActions) {
  const mapDispatchToProps = {
    onGetContest: singleContestActions.get,
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(ContestScoreboardPage));
}

export default createContestScoreboardPage(injectedSingleContestActions);
