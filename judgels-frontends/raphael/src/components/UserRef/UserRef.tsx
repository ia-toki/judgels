import * as classNames from 'classnames';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { Profile } from '../../modules/api/jophiel/profile';
import { getRatingClass } from '../../modules/api/jophiel/userRating';

import './UserRef.css';

export interface UserRefProps {
  profile: Profile;
}

export const UserRef = (props: UserRefProps) => {
  const { profile } = props;
  return (
    <Link className={classNames('user-ref', getRatingClass(profile.rating))} to={`/profiles/${profile.username}`}>
      {profile.username}
    </Link>
  );
};
