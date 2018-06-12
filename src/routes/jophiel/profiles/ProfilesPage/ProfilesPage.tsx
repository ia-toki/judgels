import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { PublicUserProfile } from '../../../../modules/api/jophiel/userProfile';
import { profilesActions as injectedProfilesActions } from '../modules/profilesActions';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';

interface ProfilesPageProps extends RouteComponentProps<{ username: string }> {
  onFetchProfile: (username: string) => Promise<PublicUserProfile>;
}

interface ProfilesPageState {
  profile?: PublicUserProfile;
}

class ProfilesPage extends React.PureComponent<ProfilesPageProps, ProfilesPageState> {
  state: ProfilesPageState = {};

  async componentDidMount() {
    const profile = await this.props.onFetchProfile(this.props.match.params.username);
    this.setState({ profile });
  }

  render() {
    if (!this.state.profile) {
      return <LoadingState />;
    } else {
      return (
        <SingleColumnLayout>
          <ContentCard>
            <div>username: {this.props.match.params.username}</div>
            <div>name: {this.state.profile.name}</div>
          </ContentCard>
        </SingleColumnLayout>
      );
    }
  }
}

export function createProfilesPage(profilesActions) {
  const mapDispatchToProps = {
    onFetchProfile: profilesActions.fetchPublic,
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(ProfilesPage));
}

export default createProfilesPage(injectedProfilesActions);
