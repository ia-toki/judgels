import * as React from 'react';

import { UserInfo } from '../../modules/api/jophiel/user';

import './UserRef.css';

export interface UserRefProps {
  user: UserInfo;
}

export const UserRef = (props: UserRefProps) => <span className="user-ref">{props.user.username}</span>;
