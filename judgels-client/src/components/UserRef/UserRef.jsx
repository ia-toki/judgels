import classNames from 'classnames';
import { Link } from 'react-router-dom';

import { getRatingClass } from '../../modules/api/jophiel/userRating';

import './UserRef.scss';

export function UserRef({ profile, showFlag }) {
  const renderFlag = () => {
    if (!showFlag) {
      return null;
    }
    if (!profile || !profile.country) {
      return <div className="user-ref__flag-dummy" />;
    }
    return (
      <img alt={profile.country} src={`/flags/flags-iso/shiny/24/${profile.country}.png`} className="user-ref__flag" />
    );
  };

  const renderUsername = () => {
    return (
      profile && (
        <Link
          className={classNames('user-ref__username', getRatingClass(profile.rating))}
          to={`/profiles/${profile.username}`}
        >
          {profile.username}
        </Link>
      )
    );
  };

  return (
    <>
      {renderFlag()}
      {renderUsername()}
    </>
  );
}
