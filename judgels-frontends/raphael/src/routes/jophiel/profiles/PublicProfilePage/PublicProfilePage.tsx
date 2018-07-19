import * as React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps, withRouter } from 'react-router';

import { PublicUserProfile } from '../../../../modules/api/jophiel/userProfile';
import { profileActions as injectedProfileActions } from '../modules/profileActions';
import { SingleColumnLayout } from '../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';

interface PublicProfilePageProps extends RouteComponentProps<{ username: string }> {
  onGetPublicProfile: (username: string) => Promise<PublicUserProfile>;
}

interface PublicProfilePageState {
  profile?: PublicUserProfile;
}

class PublicProfilePage extends React.PureComponent<PublicProfilePageProps, PublicProfilePageState> {
  state: PublicProfilePageState = {};

  async componentDidMount() {
    const profile = await this.props.onGetPublicProfile(this.props.match.params.username);
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

export function createPublicProfilePage(profileActions) {
  const mapDispatchToProps = {
    onGetPublicProfile: profileActions.getPublicProfile,
  };
  return withRouter<any>(connect(undefined, mapDispatchToProps)(PublicProfilePage));
}

export default createPublicProfilePage(injectedProfileActions);
