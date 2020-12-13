import * as React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import { selectContest } from '../modules/contestSelectors';
import { REFRESH_WEB_CONFIG_INTERVAL } from '../../../../modules/api/uriel/contestWeb';
import * as contestActions from '../modules/contestActions';
import * as contestWebActions from './modules/contestWebActions';
import * as breadcrumbsActions from '../../../../modules/breadcrumbs/breadcrumbsActions';

class SingleContestDataRoute extends React.Component {

  currentTimeout;

  async componentDidMount() {
    const { contest, match } = this.props;

    // Optimization:
    // If the current contest slug is equal to the persisted one, then assume the JID is still the same,
    if (contest && contest.slug === match.params.contestSlug) {
      this.currentTimeout = setTimeout(
        () => this.refreshWebConfig(contest.jid),
        REFRESH_WEB_CONFIG_INTERVAL
      );
    }

    // so that we don't have to wait until we get the contest from backend.
    const { contest: newContest } = await this.props.onGetContestBySlugWithWebConfig(match.params.contestSlug);
    this.props.onPushBreadcrumb(this.props.match.url, newContest.name);

    if (!contest || contest.slug !== match.params.contestSlug) {
      this.currentTimeout = setTimeout(
        () => this.refreshWebConfig(newContest.jid),
        REFRESH_WEB_CONFIG_INTERVAL
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
      REFRESH_WEB_CONFIG_INTERVAL
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
