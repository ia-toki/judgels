import * as React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

import { Card } from 'components/Card/Card';
import { Contest } from 'modules/api/uriel/contest';
import { contestActions as injectedContestActions } from 'routes/uriel/contests/modules/contestActions';

import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';
import { LoadingActiveContestCard } from '../ActiveContestCard/LoadingActiveContestCard';

import './ActiveContestsWidget.css';

interface ActiveContestsWidgetProps {
  onGetActiveContests: () => Promise<Contest[]>;
}

interface ActiveContestsWidgetState {
  contests?: Contest[];
}

class ActiveContestsWidget extends React.PureComponent<ActiveContestsWidgetProps> {
  state: ActiveContestsWidgetState = {};

  async componentDidMount() {
    const contests = await this.props.onGetActiveContests();
    this.setState({ contests });
  }

  render() {
    return (
      <Card title="Active contests" className="active-contests-widget">
        {this.renderActiveContests()}

        <small>
          <Link to={'/contests'}>See all contests...</Link>
        </small>
      </Card>
    );
  }

  private renderActiveContests = () => {
    const { contests } = this.state;
    if (!contests) {
      return <LoadingActiveContestCard />;
    }
    if (contests.length === 0) {
      return (
        <p>
          <small>No active contests.</small>
        </p>
      );
    }
    return contests.map(contest => <ActiveContestCard key={contest.jid} contest={contest} />);
  };
}

export function createActiveContestsWidget(contestActions) {
  const mapDispatchToProps = {
    onGetActiveContests: contestActions.getActiveContests,
  };
  return connect(undefined, mapDispatchToProps)(ActiveContestsWidget);
}

export default createActiveContestsWidget(injectedContestActions);
