import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';
import { push } from 'react-router-redux';

import { Contest } from '../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../uriel/contests/modules/contestActions';

interface LegacyCompetitionRouteProps extends RouteComponentProps<{ contestSlug: string }> {
  onGetContest: (contestSlug: string) => Promise<Contest>;
  onRedirect: (path: string) => any;
}

class LegacyCompetitionRoute extends React.Component<LegacyCompetitionRouteProps> {
  async componentDidMount() {
    const contest = await this.props.onGetContest(this.props.match.params.contestSlug);
    this.props.onRedirect(`/contests/${contest.slug || contest.id}`);
  }

  render() {
    return null;
  }
}

function createLegacyCompetitionRoute(contestActions) {
  const mapDispatchToProps = {
    onGetContest: contestActions.getContestBySlug,
    onRedirect: push,
  };
  return connect(undefined, mapDispatchToProps)(LegacyCompetitionRoute);
}

export default withRouter<any>(createLegacyCompetitionRoute(injectedContestActions));
