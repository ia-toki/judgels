import { HTMLTable } from '@blueprintjs/core';
import { Box } from '@blueprintjs/labs';

import { getCountryName } from '../../../../../assets/data/countries';
import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { getRatingClass } from '../../../../../modules/api/userRating';

import './BasicProfilePanel.scss';

export function BasicProfilePanel({ avatarUrl, basicProfile: { username, name, country, rating } }) {
  const renderMain = () => {
    return (
      <Box className="basic-profile-panel__main" padding={4}>
        <div className="basic-profile-panel__avatar-wrapper">
          <img className="basic-profile-panel__avatar" src={avatarUrl} alt="avatar" />
        </div>
        <p className={getRatingClass(rating)}>{username}</p>
        {renderCountry()}
      </Box>
    );
  };

  const renderCountry = () => {
    if (!country) {
      return null;
    }
    return (
      <div>
        <img alt={country} src={`/flags/flags-iso/shiny/24/${country}.png`} className="basic-profile-panel__flag" />
        <span className="basic-profile-panel__country">{getCountryName(country)}</span>
      </div>
    );
  };

  const renderDetails = () => {
    return (
      <div className="basic-profile-panel__details">
        <HTMLTable striped className="basic-profile-panel__details-table">
          <tbody>
            <tr>
              <td className="basic-profile-panel__details-keys">Name</td>
              <td>{name || '-'}</td>
            </tr>
            <tr>
              <td className="basic-profile-panel__details-keys">Rating</td>
              <td className={getRatingClass(rating)}>{(rating && rating.publicRating) || '-'}</td>
            </tr>
          </tbody>
        </HTMLTable>
      </div>
    );
  };

  return (
    <ContentCard title="Basic profile" className="profile-summary-page basic-profile-panel">
      <div className="basic-profile-panel__wrapper">
        {renderMain()}
        <div className="basic-profile-panel__divider" />
        {renderDetails()}
      </div>
    </ContentCard>
  );
}
