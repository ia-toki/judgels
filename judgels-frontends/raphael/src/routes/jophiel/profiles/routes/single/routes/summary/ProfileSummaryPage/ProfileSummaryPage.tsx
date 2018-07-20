import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { SingleColumnLayout } from '../../../../../../../../components/layouts/SingleColumnLayout/SingleColumnLayout';
import { ContentCard } from '../../../../../../../../components/ContentCard/ContentCard';
import { PublicUserProfile } from '../../../../../../../../modules/api/jophiel/userProfile';
import { AppState } from '../../../../../../../../modules/store';
import { selectPublicProfile } from '../../../../../modules/publicProfileSelectors';

interface ProfileSummaryPageProps {
  profile: PublicUserProfile;
}

class ProfileSummaryPage extends React.PureComponent<ProfileSummaryPageProps> {
  render() {
    return (
      <SingleColumnLayout>
        <ContentCard>
          <div>username: {this.props.profile.username}</div>
          <div>name: {this.props.profile.name}</div>
        </ContentCard>
      </SingleColumnLayout>
    );
  }
}

function createProfileSummaryPage() {
  const mapStateToProps = (state: AppState) => ({
    profile: selectPublicProfile(state),
  });

  return withRouter<any>(connect(mapStateToProps)(ProfileSummaryPage));
}

export default createProfileSummaryPage();
