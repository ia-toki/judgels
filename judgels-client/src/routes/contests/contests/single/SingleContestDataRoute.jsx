import { Component } from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../modules/api/uriel/contestWeb';
import { selectContest } from '../modules/contestSelectors';

import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';
import * as contestActions from '../modules/contestActions';
import * as contestWebActions from './modules/contestWebActions';

class SingleContestDataRoute extends Component {
  static GET_CONFIG_TIMEOUT = REFRESH_WEB_CONFIG_INTERVAL;

  currentTimeout;

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

  refreshWebConfig = async contestJid => {
    await this.props.onGetContestWebConfig(contestJid);
    this.currentTimeout = setTimeout(
      () => this.refreshWebConfig(contestJid),
      SingleContestDataRoute.GET_CONFIG_TIMEOUT
    );
  };
}

const mapStateToProps = state => ({
  contest: selectContest(state),
});

const mapDispatchToProps = {
  onGetContestBySlugWithWebConfig: contestWebActions.getContestBySlugWithWebConfig,
  onGetContestWebConfig: contestWebActions.getWebConfig,
  onClearContestWebConfig: contestWebActions.clearWebConfig,
  onClearContest: contestActions.clearContest,
  onPushBreadcrumb: breadcrumbsActions.pushBreadcrumb,
  onPopBreadcrumb: breadcrumbsActions.popBreadcrumb,
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(SingleContestDataRoute));
