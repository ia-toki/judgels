import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { Contest } from '../../../../../../../../modules/api/uriel/contest';
import { contestActions as injectedContestActions } from '../../../../../modules/contestActions';
import { breadcrumbsActions as injectedBreadcrumbsActions } from '../../../../../../../../modules/breadcrumbs/breadcrumbsActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestJid: string }> {
  onFetchContest: (contestJid: string) => Promise<Contest>;
  onClearContest: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  async componentDidMount() {
    await this.refresh(this.props.match.params.contestJid);
  }

  async componentWillReceiveProps(nextProps: SingleContestDataRouteProps) {
    if (nextProps.match.params.contestJid !== this.props.match.params.contestJid) {
      this.props.onPopBreadcrumb(this.props.match.url);
      await this.refresh(nextProps.match.params.contestJid);
    }
  }

  componentWillUnmount() {
    this.props.onClearContest();
    this.props.onPopBreadcrumb(this.props.match.url);
  }

  render() {
    return null;
  }

  private refresh = async (contestJid: string) => {
    const contest = await this.props.onFetchContest(contestJid);
    this.props.onPushBreadcrumb(this.props.match.url, contest.name);
  };
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
