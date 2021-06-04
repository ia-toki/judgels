import classNames from 'classnames';
import Flag from 'react-flags';
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
    return <Flag basePath="/flags" name={profile.country} format="png" pngSize={24} shiny className="user-ref__flag" />;
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
