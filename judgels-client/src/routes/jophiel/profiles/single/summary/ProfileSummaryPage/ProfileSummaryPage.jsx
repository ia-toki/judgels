import { Component } from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { isTLX } from '../../../../../../conf';
import { selectUserJid, selectUsername } from '../../../../modules/profileSelectors';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';

import * as avatarActions from '../../../../modules/avatarActions';
import * as profileActions from '../../modules/profileActions';

import './ProfileSummaryPage.scss';

class ProfileSummaryPage extends Component {
  state = {
    avatarUrl: undefined,
    basicProfile: undefined,
    userStats: undefined,
  };

  async componentDidMount() {
    await this.refreshSummary();
  }

  async componentDidUpdate(prevProps) {
    if (this.props.userJid !== prevProps.userJid) {
      await this.refreshSummary();
    }
  }

  render() {
    return (
      <>
        {this.renderBasicProfile()}
        {this.renderProblemStats()}
      </>
    );
  }

  refreshSummary = async () => {
    const [avatarUrl, basicProfile, userStats] = await Promise.all([
      this.props.onRenderAvatar(this.props.userJid),
      this.props.onGetBasicProfile(this.props.userJid),
      this.getUserStats(this.props.username),
    ]);
    this.setState({ avatarUrl, basicProfile, userStats });
  };

  renderBasicProfile = () => {
    const { avatarUrl, basicProfile } = this.state;
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return <BasicProfilePanel basicProfile={basicProfile} avatarUrl={avatarUrl} />;
  };

  renderProblemStats = () => {
    const { userStats } = this.state;
    if (!isTLX()) {
      return null;
    }
    if (!userStats) {
      return <LoadingState />;
    }

    return <ProblemStatsPanel userStats={userStats} />;
  };

  getUserStats = username => {
    if (!isTLX()) {
      return Promise.resolve(null);
    }
    return this.props.onGetUserStats(username);
  };
}

const mapStateToProps = state => ({
  userJid: selectUserJid(state),
  username: selectUsername(state),
});
const mapDispatchToProps = {
  onRenderAvatar: avatarActions.renderAvatar,
  onGetBasicProfile: profileActions.getBasicProfile,
  onGetUserStats: profileActions.getUserStats,
};

export default connect(mapStateToProps, mapDispatchToProps)(ProfileSummaryPage);
