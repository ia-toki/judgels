import { PureComponent } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';

import { Card } from '../../../../../components/Card/Card';
import { ActiveContestCard } from '../ActiveContestCard/ActiveContestCard';
import { LoadingActiveContestCard } from '../ActiveContestCard/LoadingActiveContestCard';

import * as contestActions from '../../../../contests/contests/modules/contestActions';

class ActiveContestsWidget extends PureComponent {
  state = {
    response: undefined,
  };

  async componentDidMount() {
    const response = await this.props.onGetActiveContests();
    this.setState({ response });
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

  renderActiveContests = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingActiveContestCard />;
    }

    const { data: contests, rolesMap } = response;
    if (contests.length === 0) {
      return (
        <p>
          <small>No active contests.</small>
        </p>
      );
    }
    return contests.map(contest => (
      <ActiveContestCard key={contest.jid} contest={contest} role={rolesMap[contest.jid]} />
    ));
  };
}

const mapDispatchToProps = {
  onGetActiveContests: contestActions.getActiveContests,
};
export default connect(undefined, mapDispatchToProps)(ActiveContestsWidget);
