import * as classNames from 'classnames';
import * as React from 'react';
import { Link } from 'react-router-dom';

import { UserInfo } from '../../modules/api/jophiel/user';
import { getRatingLeague } from '../../modules/api/jophiel/userRating';

import './UserRef.css';

export interface UserRefProps {
  user: UserInfo;
}

export const UserRef = (props: UserRefProps) => {
  const { user } = props;
  return (
    <Link className={classNames('user-ref', getRatingLeague(user.rating))} to={`/profiles/${user.username}`}>
      {user.username}
    </Link>
  );
};
