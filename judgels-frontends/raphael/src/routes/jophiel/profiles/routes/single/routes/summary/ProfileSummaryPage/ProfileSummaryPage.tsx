import { Card } from '@blueprintjs/core';
import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { PublicUserProfile } from '../../../../../../../../modules/api/jophiel/userProfile';
import { AppState } from '../../../../../../../../modules/store';
import { getRatingLeague } from '../../../../../../../../modules/api/jophiel/userRating';
import { selectPublicProfile } from '../../../../../modules/publicProfileSelectors';
import { avatarActions as injectedAvatarActions } from '../../../../../../modules/avatarActions';

import './ProfileSummaryPage.css';

interface ProfileSummaryPageProps {
  profile: PublicUserProfile;
  onRenderAvatar: () => Promise<string>;
}

interface ProfileSummaryPageState {
  avatarUrl?: string;
}

class ProfileSummaryPage extends React.PureComponent<ProfileSummaryPageProps, ProfileSummaryPageState> {
  state: ProfileSummaryPageState = {};

  async componentDidMount() {
    const avatarUrl = await this.props.onRenderAvatar();
    this.setState({ avatarUrl });
  }

  render() {
    return this.renderBasicProfile();
  }

  private renderBasicProfile = () => {
    const { avatarUrl } = this.state;
    if (!avatarUrl) {
      return <LoadingState />;
    }

    const { profile } = this.props;
    return (
      <Card>
        <img className="basic-profile-card__avatar" src={avatarUrl} />
        <div className="basic-profile-card__details">
          {this.renderUsername(profile)}
          <div>{profile.name}</div>
        </div>
        <div className="clearfix" />
      </Card>
    );
  };

  private renderUsername = (profile: PublicUserProfile) => {
    return <div className={getRatingLeague(profile.rating)}>{profile.username}</div>;
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
