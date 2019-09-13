import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { BasicProfilePanel } from '../BasicProfilePanel/BasicProfilePanel';
import { ContestRatingHistoryPanel } from '../ContestRatingHistoryPanel/ContestRatingHistoryPanel';
import { AppState } from '../../../../../../modules/store';
import { BasicProfile } from '../../../../../../modules/api/jophiel/profile';
import { ContestRatingHistoryResponse } from '../../../../../../modules/api/uriel/contestRating';
import { selectUserJid, selectUsername } from '../../../../modules/profileSelectors';
import { avatarActions as injectedAvatarActions } from '../../../../modules/avatarActions';
import { profileActions as injectedProfileActions } from '../../../../modules/profileActions';
import { contestRatingActions as injectedContestRatingActions } from '../../../../modules/contestRatingActions';

import './ProfileSummaryPage.css';

interface ProfileSummaryPageProps {
  userJid: string;
  username: string;
  onRenderAvatar: (userJid?: string) => Promise<string>;
  onGetBasicProfile: (userJid: string) => Promise<BasicProfile>;
  onGetContestRatingHistory: (username: string) => Promise<ContestRatingHistoryResponse>;
}

interface ProfileSummaryPageState {
  avatarUrl?: string;
  basicProfile?: BasicProfile;
  contestRatingHistory?: ContestRatingHistoryResponse;
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
        {this.renderContestRatingHistory()}
      </>
    );
  }

  private refreshSummary = async () => {
    const [avatarUrl, basicProfile, contestRatingHistory] = await Promise.all([
      this.props.onRenderAvatar(this.props.userJid),
      this.props.onGetBasicProfile(this.props.userJid),
      this.props.onGetContestRatingHistory(this.props.username),
    ]);
    this.setState({ avatarUrl, basicProfile, contestRatingHistory });
  };

  private renderBasicProfile = () => {
    const { avatarUrl, basicProfile } = this.state;
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return <BasicProfilePanel basicProfile={basicProfile} avatarUrl={avatarUrl} />;
  };

  private renderContestRatingHistory = () => {
    const { contestRatingHistory } = this.state;
    if (!contestRatingHistory) {
      return <LoadingState />;
    }

    return <ContestRatingHistoryPanel history={contestRatingHistory} />;
  };
}

function createProfileSummaryPage(avatarActions, profileActions, contestRatingActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
    username: selectUsername(state),
  });
  const mapDispatchToProps = {
    onRenderAvatar: avatarActions.renderAvatar,
    onGetBasicProfile: profileActions.getBasicProfile,
    onGetContestRatingHistory: contestRatingActions.getRatingHistory,
  };

  return withRouter<any, any>(connect(mapStateToProps, mapDispatchToProps)(ProfileSummaryPage));
}

export default createProfileSummaryPage(injectedAvatarActions, injectedProfileActions, injectedContestRatingActions);
