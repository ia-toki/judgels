import * as React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';

import { Card } from '../../../../components/Card/Card';
import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';
import { LoadingActiveContestCard } from '../ActiveContestCard/LoadingActiveContestCard';
import { Contest } from '../../../../modules/api/uriel/contest';
import { activeContestWidgetActions as injectedActiveContestWidgetActions } from '../modules/activeContestsWidgetActions';

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
      <Card title="Active contests">
        {this.renderActiveContests()}

        <Link to={'/contests'}>
          <small>See all contests...</small>
        </Link>
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

export function createActiveContestsWidget(activeContestsWidgetActions) {
  const mapDispatchToProps = {
    onGetActiveContests: activeContestsWidgetActions.getActiveContests,
  };
  return connect(undefined, mapDispatchToProps)(ActiveContestsWidget);
}

export default createActiveContestsWidget(injectedActiveContestWidgetActions);
