import { Card } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { PublicUserProfile } from '../../../../../../../../modules/api/jophiel/userProfile';
import { AppState } from '../../../../../../../../modules/store';
import { getRatingLeague } from '../../../../../../../../modules/api/jophiel/userRating';
import { selectPublicProfile } from '../../../../../../modules/publicProfileSelectors';
import { avatarActions as injectedAvatarActions } from '../../../../../../modules/avatarActions';

import './ProfileSummaryPage.css';

interface ProfileSummaryPageProps {
  profile: PublicUserProfile;
  onRenderAvatar: (userJid?: string) => Promise<string>;
}

interface ProfileSummaryPageState {
  avatarUrl?: string;
}

class ProfileSummaryPage extends React.PureComponent<ProfileSummaryPageProps, ProfileSummaryPageState> {
  state: ProfileSummaryPageState = {};

  async componentDidMount() {
    await this.refreshSummary();
  }

  async componentDidUpdate(prevProps: ProfileSummaryPageProps) {
    if (this.props.profile !== prevProps.profile) {
      await this.refreshSummary();
    }
  }

  render() {
    return this.renderBasicProfile();
  }

  private refreshSummary = async () => {
    const avatarUrl = await this.props.onRenderAvatar(this.props.profile.userJid);
    this.setState({ avatarUrl });
  };

  private renderBasicProfile = () => {
    const { avatarUrl } = this.state;
    if (!avatarUrl) {
      return <LoadingState />;
    }

    const { profile } = this.props;
    return (
      <Card className="basic-profile-card">
        <img className="basic-profile-card__avatar" src={avatarUrl} />
        <div className="basic-profile-card__details">
          <div>{this.renderUsername(profile)}</div>
          <div>{this.renderName(profile)}</div>
          <div>{this.renderCountry(profile)}</div>
        </div>
        <div className="clearfix" />
      </Card>
    );
  };

  private renderUsername = (profile: PublicUserProfile) => {
    return <span className={getRatingLeague(profile.rating)}>{profile.username}</span>;
  };

  private renderName = (profile: PublicUserProfile) => {
    return profile.name;
  };

  private renderCountry = (profile: PublicUserProfile) => {
    return profile.country;
  };
}

function createProfileSummaryPage(avatarActions) {
  const mapStateToProps = (state: AppState) => ({
    profile: selectPublicProfile(state),
  });
  const mapDispatchToProps = {
    onRenderAvatar: avatarActions.renderAvatar,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ProfileSummaryPage));
}

export default createProfileSummaryPage(injectedAvatarActions);
