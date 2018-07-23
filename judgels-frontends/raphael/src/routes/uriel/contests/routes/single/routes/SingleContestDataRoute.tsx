import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { Contest } from '../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../modules/contestActions';
import { contestWebConfigActions as injectedContestWebConfigActions } from '../modules/contestWebConfigActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestId: string }> {
  onGetContest: (contestId: number) => Promise<Contest>;
  onClearContest: () => void;
  onGetContestWebConfig: (contestJid: string) => void;
  onClearContestWebConfig: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  private currentTimeout;

  async componentDidMount() {
    const contest = await this.props.onGetContest(+this.props.match.params.contestId);
    this.props.onPushBreadcrumb(this.props.match.url, contest.name);

    await this.refreshWebConfig(contest.jid);
  }

  componentWillUnmount() {
    this.props.onClearContest();
    this.props.onClearContestWebConfig();
    this.props.onPopBreadcrumb(this.props.match.url.replace(/\/+$/, ''));

    if (this.currentTimeout) {
      clearTimeout(this.currentTimeout);
    }
  }

  render() {
    return null;
  }

  private refreshWebConfig = async (contestJid: string) => {
    await this.props.onGetContestWebConfig(contestJid);
    this.currentTimeout = setTimeout(() => this.refreshWebConfig(contestJid), 20000);
  };
}

export function createSingleContestDataRoute(contestActions, contestWebConfigActions, breadcrumbsActions) {
  const mapDispatchToProps = {
    onGetContest: contestActions.getContestById,
    onGetContestWebConfig: contestWebConfigActions.getWebConfig,
    onClearContestWebConfig: contestWebConfigActions.clearConfig,
    onClearContest: contestActions.clearContest,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(SingleContestDataRoute));
}

export default createSingleContestDataRoute(
  injectedContestActions,
  injectedContestWebConfigActions,
  injectedBreadcrumbsActions
);
