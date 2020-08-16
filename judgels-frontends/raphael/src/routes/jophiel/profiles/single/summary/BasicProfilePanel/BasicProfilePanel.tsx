import { HTMLTable } from '@blueprintjs/core';
import * as React from 'react';
import Flag from 'react-flags';

import { Card } from '../../../../../../components/Card/Card';
import { BasicProfile } from '../../../../../../modules/api/jophiel/profile';
import { getRatingClass } from '../../../../../../modules/api/jophiel/userRating';
import { getCountryName } from '../../../../../../assets/data/countries';

import './BasicProfilePanel.css';

export interface BasicProfilePanelProps {
  basicProfile: BasicProfile;
  avatarUrl: string;
}

export class BasicProfilePanel extends React.PureComponent<BasicProfilePanelProps> {
  render() {
    const { avatarUrl, basicProfile } = this.props;

    return (
      <Card title="Basic profile" className="profile-summary-card basic-profile-card">
        <div className="basic-profile-card__wrapper">
          {this.renderMain(basicProfile, avatarUrl)}
          <div className="basic-profile-card__divider" />
          {this.renderDetails(basicProfile)}
          <div className="clearfix" />
        </div>
      </Card>
    );
  }

  private renderMain = (profile: BasicProfile, avatarUrl: string) => {
    return (
      <div className="basic-profile-card__main">
        <div className="basic-profile-card__avatar-wrapper">
          <img className="basic-profile-card__avatar" src={avatarUrl} alt="avatar" />
        </div>
        <p className={getRatingClass(profile.rating)}>{profile.username}</p>
        {this.renderCountry(profile)}
      </div>
    );
  };

  private renderCountry = (profile: BasicProfile) => {
    if (!profile.country) {
      return null;
    }
    return (
      <div>
        <Flag
          basePath="/flags"
          name={profile.country}
          format="png"
          pngSize={24}
          shiny
          className="basic-profile-card__flag"
        />
        <span className="basic-profile-card__country">{getCountryName(profile.country)}</span>
      </div>
    );
  };

  private renderDetails = (profile: BasicProfile) => {
    return (
      <div className="basic-profile-card__details">
        <HTMLTable striped className="basic-profile-card__details-table">
          <tbody>
            <tr>
              <td className="basic-profile-card__details-keys">Name</td>
              <td>{profile.name || '-'}</td>
            </tr>
            <tr>
              <td className="basic-profile-card__details-keys">Rating</td>
              <td className={getRatingClass(profile.rating)}>
                {(profile.rating && profile.rating.publicRating) || '-'}
              </td>
            </tr>
          </tbody>
        </HTMLTable>
      </div>
    );
  };
}
