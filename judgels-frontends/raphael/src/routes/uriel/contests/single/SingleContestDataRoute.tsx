import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { breadcrumbsActions as injectedBreadcrumbsActions } from 'modules/breadcrumbs/breadcrumbsActions';

import { selectContest } from '../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../modules/contestActions';
import { contestWebConfigActions as injectedContestWebConfigActions } from './modules/contestWebConfigActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestSlug: string }> {
  contest?: Contest;

  onGetContest: (contestSlug: string) => Promise<Contest>;
  onClearContest: () => void;
  onGetContestWebConfig: (contestJid: string) => void;
  onClearContestWebConfig: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  private currentTimeout;

  async componentDidMount() {
    const { contest, match } = this.props;

    // Optimization:
    // If the current contest slug is equal to the persisted one, then assume the JID is still the same,
    if (contest && contest.slug === match.params.contestSlug) {
      this.refreshWebConfig(contest.jid);
    }

    // so that we don't have to wait until we get the contest from backend.
    const newContest = await this.props.onGetContest(match.params.contestSlug);
    this.props.onPushBreadcrumb(this.props.match.url, newContest.name);

    if (!contest || contest.slug !== match.params.contestSlug) {
      this.refreshWebConfig(newContest.jid);
    }
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
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state),
    } as Partial<SingleContestDataRouteProps>);

  const mapDispatchToProps = {
    onGetContest: contestActions.getContestBySlug,
    onGetContestWebConfig: contestWebConfigActions.getWebConfig,
    onClearContestWebConfig: contestWebConfigActions.clearConfig,
    onClearContest: contestActions.clearContest,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SingleContestDataRoute));
}

export default createSingleContestDataRoute(
  injectedContestActions,
  injectedContestWebConfigActions,
  injectedBreadcrumbsActions
);
