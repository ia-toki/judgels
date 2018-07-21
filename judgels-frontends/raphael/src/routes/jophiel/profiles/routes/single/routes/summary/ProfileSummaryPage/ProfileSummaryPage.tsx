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
import { Card } from '../../../../../../../../components/Card/Card';

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
      <Card title="Basic profile" className="basic-profile-card">
        {this.renderMain(profile, avatarUrl)}
        <div className="basic-profile-card__divider" />
        {this.renderDetails(profile)}
        <div className="clearfix" />
      </Card>
    );
  };

  private renderMain = (profile: PublicUserProfile, avatarUrl: string) => {
    return (
      <div className="basic-profile-card__main">
        <img className="basic-profile-card__avatar" src={avatarUrl} />
        <p className={getRatingLeague(profile.rating)}>{profile.username}</p>
        <p>{profile.nationality}</p>
      </div>
    );
  };

  private renderDetails = (profile: PublicUserProfile) => {
    return (
      <div className="basic-profile-card__details">
        <div className="basic-profile-card__details-keys">
          {profile.name && <p>Name:</p>}
          <p>Rating:</p>
        </div>
        <div className="basic-profile-card__details-values">
          {profile.name && <p>{profile.name}</p>}
          <p>{profile.rating || '-'}</p>
        </div>
        <div className="clearfix" />
      </div>
    );
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
