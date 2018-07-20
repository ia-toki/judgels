import * as React from 'react';
import { connect } from 'react-redux';

import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { UserProfile } from '../../../../../../modules/api/jophiel/userProfile';
import { ProfilePanel } from '../../../../panels/profile/ProfilePanel/ProfilePanel';
import { AppState } from '../../../../../../modules/store';
import { selectUserJid } from '../../../../../../modules/session/sessionSelectors';
import { profileActions as injectedProfileActions } from '../../../../modules/profileActions';

interface ProfilePageProps {
  onGetProfile: () => Promise<UserProfile>;
  onUpdateProfile: (profile: UserProfile) => Promise<void>;
}

interface ProfilePageState {
  profile?: UserProfile;
}

class ProfilePage extends React.PureComponent<ProfilePageProps, ProfilePageState> {
  state: ProfilePageState = {};

  async componentDidMount() {
    await this.refreshProfile();
  }

  render() {
    if (!this.state.profile) {
      return <LoadingState />;
    }
    return <ProfilePanel profile={this.state.profile} onUpdateProfile={this.onUpdateProfile} />;
  }

  private refreshProfile = async () => {
    const profile = await this.props.onGetProfile();
    this.setState({ profile });
  };

  private onUpdateProfile = async (profile: UserProfile) => {
    await this.props.onUpdateProfile(profile);
    await this.refreshProfile();
  };
}

export function createProfilePage(profileActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
  });
  const mapDispatchToProps = {
    onGetProfile: profileActions.getProfile,
    onUpdateProfile: profileActions.updateProfile,
  };
  const mergeProps = (stateProps, dispatchProps) => ({
    onGetProfile: () => dispatchProps.onGetProfile(stateProps.userJid),
    onUpdateProfile: (profile: UserProfile) => dispatchProps.onUpdateProfile(stateProps.userJid, profile),
  });
  return connect<any>(mapStateToProps, mapDispatchToProps, mergeProps)(ProfilePage);
}

export default withBreadcrumb('Profile')(createProfilePage(injectedProfileActions));
