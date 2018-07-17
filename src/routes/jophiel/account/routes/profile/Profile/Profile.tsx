import * as React from 'react';
import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { UserProfile } from '../../../../../../modules/api/jophiel/user';
import { ProfilePanel } from '../../../../panels/profile/Profile/Profile';
import { AppState } from '../../../../../../modules/store';
import { selectProfile } from '../../../../../../modules/session/sessionSelectors';
import { profileActions as injectedProfileActions } from '../modules/profileActions';

interface ProfileContainerProps {
  profile: UserProfile | undefined;
  onGetProfile: () => Promise<void>;
  onUpdateProfile: (profile: UserProfile) => Promise<void>;
}

class ProfileContainer extends React.Component<ProfileContainerProps> {
  async componentDidMount() {
    await this.props.onGetProfile();
  }

  render() {
    if (!this.props.profile) {
      return null;
    }
    return <ProfilePanel profile={this.props.profile} onUpdateProfile={this.props.onUpdateProfile} />;
  }
}

export function createProfileContainer(profileActions) {
  const mapStateToProps = (state: AppState) => ({
    profile: selectProfile(state),
  });
  const mapDispatchToProps = dispatch => ({
    onGetProfile: () => dispatch(profileActions.get()),
    onUpdateProfile: (profile: UserProfile) => dispatch(profileActions.update(profile)),
  });
  return connect(mapStateToProps, mapDispatchToProps)(ProfileContainer);
}

export default withBreadcrumb('Profile')(createProfileContainer(injectedProfileActions));
