import * as React from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ProblemStatsPanel } from '../ProblemStatsPanel/ProblemStatsPanel';
import { AppState } from '../../../../../../modules/store';
import { BasicProfile } from '../../../../../../modules/api/jophiel/profile';
import { UserStats } from '../../../../../../modules/api/jerahmeel/user';
import { selectUserJid, selectUsername } from '../../../../modules/profileSelectors';
import * as avatarActions from '../../../../modules/avatarActions';
import * as profileActions from '../../modules/profileActions';

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
        {this.renderProblemStats()}
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

  private renderProblemStats = () => {
    const { userStats } = this.state;
    if (!userStats) {
      return <LoadingState />;
    }

    return <ProblemStatsPanel userStats={userStats} />;
  };
}

const mapStateToProps = (state: AppState) => ({
  userJid: selectUserJid(state),
  username: selectUsername(state),
});
const mapDispatchToProps = {
  onRenderAvatar: avatarActions.renderAvatar,
  onGetBasicProfile: profileActions.getBasicProfile,
  onGetUserStats: profileActions.getUserStats,
};

export default connect(mapStateToProps, mapDispatchToProps)(ProfileSummaryPage);
