import { HTMLTable } from '@blueprintjs/core';
import Flag from 'react-flags';

import { Card } from '../../../../../../components/Card/Card';
import { getRatingClass } from '../../../../../../modules/api/jophiel/userRating';
import { getCountryName } from '../../../../../../assets/data/countries';

import './BasicProfilePanel.css';

export function BasicProfilePanel({ avatarUrl, basicProfile: { username, name, country, rating } }) {
  const renderMain = () => {
    return (
      <div className="basic-profile-card__main">
        <div className="basic-profile-card__avatar-wrapper">
          <img className="basic-profile-card__avatar" src={avatarUrl} alt="avatar" />
        </div>
        <p className={getRatingClass(rating)}>{username}</p>
        {renderCountry()}
      </div>
    );
  };

  const renderCountry = () => {
    if (!country) {
      return null;
    }
    return (
      <div>
        <Flag basePath="/flags" name={country} format="png" pngSize={24} shiny className="basic-profile-card__flag" />
        <span className="basic-profile-card__country">{getCountryName(country)}</span>
      </div>
    );
  };

  const renderDetails = () => {
    return (
      <div className="basic-profile-card__details">
        <HTMLTable striped className="basic-profile-card__details-table">
          <tbody>
            <tr>
              <td className="basic-profile-card__details-keys">Name</td>
              <td>{name || '-'}</td>
            </tr>
            <tr>
              <td className="basic-profile-card__details-keys">Rating</td>
              <td className={getRatingClass(rating)}>{(rating && rating.publicRating) || '-'}</td>
            </tr>
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  return (
    <Card title="Basic profile" className="profile-summary-card basic-profile-card">
      <div className="basic-profile-card__wrapper">
        {renderMain()}
        <div className="basic-profile-card__divider" />
        {renderDetails()}
        <div className="clearfix" />
      </div>
    </Card>
  );
}
