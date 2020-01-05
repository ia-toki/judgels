import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { UserStatsPanel } from '../UserStatsPanel/UserStatsPanel';
import { AppState } from '../../../../../../modules/store';
import { BasicProfile } from '../../../../../../modules/api/jophiel/profile';
import { UserStats } from '../../../../../../modules/api/jerahmeel/user';
import { selectUserJid, selectUsername } from '../../../../modules/profileSelectors';
import { avatarActions as injectedAvatarActions } from '../../../../modules/avatarActions';
import { profileActions as injectedProfileActions } from '../../modules/profileActions';

import './ProfileSummaryPage.css';

interface ProfileSummaryPageProps {
  userJid: string;
  username: string;
  onRenderAvatar: (userJid?: string) => Promise<string>;
  onGetBasicProfile: (userJid: string) => Promise<BasicProfile>;
  onGetUserStats: (username: string) => Promise<UserStats>;
}

interface ProfileSummaryPageState {
  avatarUrl?: string;
  basicProfile?: BasicProfile;
  userStats?: UserStats;
}

class ProfileSummaryPage extends React.PureComponent<ProfileSummaryPageProps, ProfileSummaryPageState> {
  state: ProfileSummaryPageState = {};

  async componentDidMount() {
    await this.refreshSummary();
  }

  async componentDidUpdate(prevProps: ProfileSummaryPageProps) {
    if (this.props.userJid !== prevProps.userJid) {
      await this.refreshSummary();
    }
  }

  render() {
    return (
      <>
        {this.renderBasicProfile()}
        {this.renderUserStats()}
      </>
    );
  }

  private refreshSummary = async () => {
    const [avatarUrl, basicProfile, userStats] = await Promise.all([
      this.props.onRenderAvatar(this.props.userJid),
      this.props.onGetBasicProfile(this.props.userJid),
      this.props.onGetUserStats(this.props.username),
    ]);
    this.setState({ avatarUrl, basicProfile, userStats });
  };

  private renderBasicProfile = () => {
    const { avatarUrl, basicProfile } = this.state;
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return <BasicProfilePanel basicProfile={basicProfile} avatarUrl={avatarUrl} />;
  };

  private renderUserStats = () => {
    const { userStats } = this.state;
    if (!userStats) {
      return <LoadingState />;
    }

    return <UserStatsPanel userStats={userStats} />;
  };
}

function createProfileSummaryPage(avatarActions, profileActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
    username: selectUsername(state),
  });
  const mapDispatchToProps = {
    onRenderAvatar: avatarActions.renderAvatar,
    onGetBasicProfile: profileActions.getBasicProfile,
    onGetUserStats: profileActions.getUserStats,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProfileSummaryPage));
}

export default createProfileSummaryPage(injectedAvatarActions, injectedProfileActions);
