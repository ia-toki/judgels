import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestJid: string }> {
  onFetchContest: (contestJid: string) => Promise<Contest>;
  onClearContest: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  async componentDidMount() {
    const contest = await this.props.onFetchContest(this.props.match.params.contestJid);
    this.props.onPushBreadcrumb(this.props.match.url, contest.name);
  }

  componentWillUnmount() {
    this.props.onClearContest();
    this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));
  }

  render() {
    return null;
  }
}

export function createSingleContestDataRoute(contestActions, breadcrumbsActions) {
  const mapDispatchToProps = {
    onFetchContest: contestActions.fetch,
    onClearContest: contestActions.clear,
    onPushBreadcrumb: breadcrumbsActions.push,
    onPopBreadcrumb: breadcrumbsActions.pop,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(SingleContestDataRoute));
}

export default createSingleContestDataRoute(injectedContestActions, injectedBreadcrumbsActions);
