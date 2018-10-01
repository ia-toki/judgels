import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';

import { AppState } from 'modules/store';
import { Contest } from 'modules/api/uriel/contest';
import { ContestWithWebConfig } from 'modules/api/uriel/contestWeb';
import { breadcrumbsActions as injectedBreadcrumbsActions } from 'modules/breadcrumbs/breadcrumbsActions';

import { selectContest } from '../modules/contestSelectors';
import { contestActions as injectedContestActions } from '../modules/contestActions';
import { contestWebActions as injectedContestWebActions } from './modules/contestWebActions';

export interface SingleContestDataRouteProps extends RouteComponentProps<{ contestSlug: string }> {
  contest?: Contest;

  onClearContest: () => void;
  onGetContestBySlugWithWebConfig: (contestJid: string) => Promise<ContestWithWebConfig>;
  onGetContestWebConfig: (contestJid: string) => void;
  onClearContestWebConfig: () => void;
  onPushBreadcrumb: (link: string, title: string) => void;
  onPopBreadcrumb: (link: string) => void;
}

class SingleContestDataRoute extends React.Component<SingleContestDataRouteProps> {
  private static GET_CONFIG_TIMEOUT = 20000;

  private currentTimeout;

  async componentDidMount() {
    const { contest, match } = this.props;

    // Optimization:
    // If the current contest slug is equal to the persisted one, then assume the JID is still the same,
    if (contest && contest.slug === match.params.contestSlug) {
      this.currentTimeout = setTimeout(
        () => this.refreshWebConfig(contest.jid),
        SingleContestDataRoute.GET_CONFIG_TIMEOUT
      );
    }

    // so that we don't have to wait until we get the contest from backend.
    const { contest: newContest } = await this.props.onGetContestBySlugWithWebConfig(match.params.contestSlug);
    this.props.onPushBreadcrumb(this.props.match.url, newContest.name);

    if (!contest || contest.slug !== match.params.contestSlug) {
      this.currentTimeout = setTimeout(
        () => this.refreshWebConfig(newContest.jid),
        SingleContestDataRoute.GET_CONFIG_TIMEOUT
      );
    }
  }

  componentWillUnmount() {
    this.props.onClearContest();
    this.props.onClearContestWebConfig();
    this.props.onPopBreadcrumb(this.props.match.url);

    if (this.currentTimeout) {
      clearTimeout(this.currentTimeout);
    }
  }

  render() {
    return null;
  }

  private refreshWebConfig = async (contestJid: string) => {
    await this.props.onGetContestWebConfig(contestJid);
    this.currentTimeout = setTimeout(
      () => this.refreshWebConfig(contestJid),
      SingleContestDataRoute.GET_CONFIG_TIMEOUT
    );
  };
}

export function createSingleContestDataRoute(contestActions, contestWebActions, breadcrumbsActions) {
  const mapStateToProps = (state: AppState) =>
    ({
      contest: selectContest(state),
    } as Partial<SingleContestDataRouteProps>);

  const mapDispatchToProps = {
    onGetContestBySlugWithWebConfig: contestWebActions.getContestBySlugWithWebConfig,
    onGetContestWebConfig: contestWebActions.getWebConfig,
    onClearContestWebConfig: contestWebActions.clearWebConfig,
    onClearContest: contestActions.clearContest,
    onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
    onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(SingleContestDataRoute));
}

export default createSingleContestDataRoute(
  injectedContestActions,
  injectedContestWebActions,
  injectedBreadcrumbsActions
);
