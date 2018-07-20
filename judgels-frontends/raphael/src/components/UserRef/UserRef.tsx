import * as React from 'react';
import { Link } from 'react-router-dom';

import { LeagueColor } from '../LeagueColor/LeagueColor';
import { UserInfo } from '../../modules/api/jophiel/user';

import './UserRef.css';

export interface UserRefProps {
  user: UserInfo;
}

export const UserRef = (props: UserRefProps) => (
  <LeagueColor rating={props.user.rating}>
    <Link to={`/profiles/${props.user.username}`} className="user-ref">
      {props.user.username}
    </Link>
  </LeagueColor>
);
