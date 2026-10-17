import { Box } from '@blueprintjs/labs';
import { Link } from '@tanstack/react-router';
import classNames from 'classnames';

import { getRatingClass } from '../../modules/api/userRating';

import './UserRef.scss';

export function UserRef({ profile, showFlag, useAnchor }) {
  const renderFlag = () => {
    if (!showFlag) {
      return null;
    }
    if (!profile || !profile.country) {
      return <Box className="user-ref__flag-dummy" display="inline-block" marginRight={2} />;
    }
    return (
      <img alt={profile.country} src={`/flags/flags-iso/shiny/24/${profile.country}.png`} className="user-ref__flag" />
    );
  };

  const renderUsername = () => {
    if (!profile) {
      return null;
    }

    const className = classNames('user-ref__username', getRatingClass(profile.rating));
    const to = `/profiles/${profile.username}`;

    // Use plain anchor when rendering outside router context (e.g., renderToString)
    if (useAnchor) {
      return (
        <a className={className} href={to}>
          {profile.username}
        </a>
      );
    }

    return (
      <Link className={className} to={to}>
        {profile.username}
      </Link>
    );
  };

  return (
    <>
      {renderFlag()}
      {renderUsername()}
    </>
  );
}
