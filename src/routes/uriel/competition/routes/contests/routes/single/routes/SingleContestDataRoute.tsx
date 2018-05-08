import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';
import { contestWebConfigActions as injectedContestWebConfigActions } from '../modules/contestWebConfigActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestId: string }> {
  onFetchContest: (contestId: number) => Promise<Contest>;
  onFetchContestWebConfig: (contestJid: string) => Promise<void>;
  onClearContest: () => void;
  onClearContestWebConfig: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  async componentDidMount() {
    const contest = await this.props.onFetchContest(+this.props.match.params.contestId);
    await this.props.onFetchContestWebConfig(contest.jid);
    this.props.onPushBreadcrumb(this.props.match.url, contest.name);
  }

  componentWillUnmount() {
    this.props.onClearContest();
    this.props.onClearContestWebConfig();
    this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));
  }

  render() {
    return null;
  }
}

export function createSingleContestDataRoute(contestActions, contestWebConfigActions, breadcrumbsActions) {
  const mapDispatchToProps = {
    onFetchContest: contestActions.fetchById,
    onFetchContestWebConfig: contestWebConfigActions.fetch,
    onClearContestWebConfig: contestWebConfigActions.clear,
    onClearContest: contestActions.clear,
    onPushBreadcrumb: breadcrumbsActions.push,
    onPopBreadcrumb: breadcrumbsActions.pop,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(SingleContestDataRoute));
}

export default createSingleContestDataRoute(
  injectedContestActions,
  injectedContestWebConfigActions,
  injectedBreadcrumbsActions
);
