import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { Card } from '../../../../../../../../components/Card/Card';
import { LoadingState } from '../../../../../../../../components/LoadingState/LoadingState';
import { BasicProfile } from '../../../../../../../../modules/api/jophiel/profile';
import { AppState } from '../../../../../../../../modules/store';
import { getRatingClass } from '../../../../../../../../modules/api/jophiel/userRating';
import { selectUserJid } from '../../../../../../modules/profileSelectors';
import { avatarActions as injectedAvatarActions } from '../../../../../../modules/avatarActions';
import { profileActions as injectedProfileActions } from '../../../../../../modules/profileActions';

import './ProfileSummaryPage.css';

interface ProfileSummaryPageProps {
  userJid: string;
  onRenderAvatar: (userJid?: string) => Promise<string>;
  onGetBasicProfile: (userJid: string) => Promise<BasicProfile>;
}

interface ProfileSummaryPageState {
  basicProfile?: BasicProfile;
  avatarUrl?: string;
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
    return this.renderBasicProfile();
  }

  private refreshSummary = async () => {
    const [avatarUrl, basicProfile] = await Promise.all([
      this.props.onRenderAvatar(this.props.userJid),
      this.props.onGetBasicProfile(this.props.userJid),
    ]);
    this.setState({ avatarUrl, basicProfile });
  };

  private renderBasicProfile = () => {
    const { avatarUrl, basicProfile } = this.state;
    if (!avatarUrl || !basicProfile) {
      return <LoadingState />;
    }

    return (
      <Card title="Basic profile" className="basic-profile-card">
        <div className="basic-profile-card__wrapper">
          {this.renderMain(basicProfile, avatarUrl)}
          <div className="basic-profile-card__divider" />
          {this.renderDetails(basicProfile)}
          <div className="clearfix" />
        </div>
      </Card>
    );
  };

  private renderMain = (profile: BasicProfile, avatarUrl: string) => {
    return (
      <div className="basic-profile-card__main">
        <div className="basic-profile-card__avatar-wrapper">
          <img className="basic-profile-card__avatar" src={avatarUrl} />
        </div>
        <p className={getRatingClass(profile.rating)}>{profile.username}</p>
        <p>{profile.nationality}</p>
      </div>
    );
  };

  private renderDetails = (profile: BasicProfile) => {
    return (
      <div className="basic-profile-card__details">
        <table className="pt-html-table pt-html-table-striped basic-profile-card__details-table">
          <tbody>
            <tr>
              <td className="basic-profile-card__details-keys">Name</td>
              <td>{profile.name || '-'}</td>
            </tr>
            <tr>
              <td className="basic-profile-card__details-keys">Rating</td>
              <td className={getRatingClass(profile.rating)}>{profile.rating || '-'}</td>
            </tr>
          </tbody>
        </table>
      </div>
    );
  };
}

function createProfileSummaryPage(avatarActions, profileActions) {
  const mapStateToProps = (state: AppState) => ({
    userJid: selectUserJid(state),
  });
  const mapDispatchToProps = {
    onRenderAvatar: avatarActions.renderAvatar,
    onGetBasicProfile: profileActions.getBasicProfile,
  };

  return withRouter<any>(connect(mapStateToProps, mapDispatchToProps)(ProfileSummaryPage));
}

export default createProfileSummaryPage(injectedAvatarActions, injectedProfileActions);
